package com.jiade.massageshopmanagement.service;

import com.jiade.massageshopmanagement.dto.*;
import com.jiade.massageshopmanagement.model.ConsumeItem;
import com.jiade.massageshopmanagement.model.ConsumeRecord;
import com.jiade.massageshopmanagement.model.ConsumeServiceTable;
import com.jiade.massageshopmanagement.model.Member;
import org.hibernate.validator.constraintvalidators.RegexpURLValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jiade.massageshopmanagement.mapper.ConsumeMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Collections;

@Service
public class ConsumeService {
    @Autowired
    private ConsumeMapper consumeMapper;

    public ConsumeListData getConsumeRecords(String keyword, LocalDateTime startDate, LocalDateTime endDate,
                                             BigDecimal minAmount, BigDecimal maxAmount, String sortBy, String order,
                                             int page, int size) {
        int offset = (page - 1) * size;
        // 1. 查消费记录（分页）
        List<ConsumptionRecordDetail> records = consumeMapper.selectConsumptionRecordsPage(
                keyword, minAmount, maxAmount, startDate, endDate, sortBy, order, offset, size);
        if (records.isEmpty()) {
            return new ConsumeListData(records, 0, 0, page);
        }
        List<Long> recordIds = records.stream().map(ConsumptionRecordDetail::getRecordId).collect(Collectors.toList());

        // 2. 查项目
        List<ConsumedProjectInfo> projects = consumeMapper.selectProjectsByConsumeRecordIds(recordIds);
        List<Long> itemIds = projects.stream().map(ConsumedProjectInfo::getConsumeItemId).collect(Collectors.toList());

        // 3. 查员工
        List<ConsumeServiceDetail> services = itemIds.isEmpty() ? Collections.emptyList()
                : consumeMapper.selectEmployeesByConsumeItemIds(itemIds);

        // 4. 分组
        Map<Long, List<ConsumedProjectInfo>> recordIdToProjects = projects.stream()
                .collect(Collectors.groupingBy(ConsumedProjectInfo::getConsumeRecordId));
        Map<Long, List<ConsumeServiceDetail>> itemIdToServices = services.stream()
                .collect(Collectors.groupingBy(ConsumeServiceDetail::getConsumeItemId));

        // 5. 组装数据
        String autoDetailMarker = "【自动详情】";
        for (ConsumptionRecordDetail record : records) {
            List<ConsumedProjectInfo> recordProjects = recordIdToProjects.getOrDefault(record.getRecordId(), Collections.emptyList());

            // 给项目设置员工列表
            BigDecimal sum = BigDecimal.ZERO;
            StringBuilder autoDetailBuilder = new StringBuilder();
            for (ConsumedProjectInfo project : recordProjects) {
                List<ConsumeServiceDetail> projectServices = itemIdToServices.getOrDefault(project.getConsumeItemId(), Collections.emptyList());
                List<String> employeeNames = projectServices.stream()
                        .map(ConsumeServiceDetail::getEmployeeName)
                        .collect(Collectors.toList());
                project.setEmployees(employeeNames);

                // 拼接自动详情
                autoDetailBuilder.append(project.getProjectName()).append("（");
                for (int i = 0; i < projectServices.size(); i++) {
                    ConsumeServiceDetail s = projectServices.get(i);
                    autoDetailBuilder.append(s.getEmployeeName()).append(":").append(s.getEarnings());
                    if (i < projectServices.size() - 1) autoDetailBuilder.append("、");
                }
                autoDetailBuilder.append("）").append(project.getPrice()).append("元，");
                sum = sum.add(project.getPrice());
            }
            if (autoDetailBuilder.length() > 0) {
                autoDetailBuilder.deleteCharAt(autoDetailBuilder.length() - 1); // 去掉最后一个逗号
                autoDetailBuilder.append("，共计").append(sum).append("元");
            }
            String autoDetail = autoDetailBuilder.toString();

            // 处理 record_detail：去掉旧的自动详情，只保留原始内容
            String originalDetail = record.getRecordDetail();
            if (originalDetail == null) originalDetail = "";
            if (originalDetail.contains(autoDetailMarker)) {
                originalDetail = originalDetail.substring(0, originalDetail.indexOf(autoDetailMarker));
                // 去除末尾多余分号
                originalDetail = originalDetail.replaceAll("[；;]+$", "");
            }
            // 拼接新自动详情
            if (!autoDetail.isEmpty()) {
                if (!originalDetail.isEmpty()) {
                    record.setRecordDetail(originalDetail + "；" + autoDetailMarker + autoDetail);
                } else {
                    record.setRecordDetail(autoDetailMarker + autoDetail);
                }
            } else {
                record.setRecordDetail(originalDetail);
            }
            // 设置项目列表
            record.setProjects(recordProjects);
        }

        int totalRecords = consumeMapper.countConsumptionRecords(
                keyword, minAmount, maxAmount, startDate, endDate);
        int totalPages = (int) Math.ceil((double) totalRecords / size);

        return new ConsumeListData(records, totalRecords, totalPages, page);
    }

    @Transactional
    public void addConsumeRecord(ConsumeRecordRequest request) {
        try {
            // 1. 组装并插入 ConsumeRecord
            ConsumeRecord record = new ConsumeRecord();
            record.setUserType(request.getMemberId() != null ? "MEMBER" : "GUEST");
            record.setUserId(request.getMemberId());
            record.setDescription(request.getCustomerDesc());
            record.setTotalPrice(request.getTotalPrice());
            record.setConsumeTime(request.getConsumeTime()); // 如需LocalDateTime
            record.setRecordDetail(request.getRecordDetail());
            record.setIsDeleted(false);
            // createTime、updateTime 可数据库自动
            consumeMapper.insertConsumeRecord(record);
            Long recordId = record.getId();

            // 2. 遍历 projects，插入 ConsumeItem
            for (ProjectInfo project : request.getProjects()) {
                // 获取项目ID
                Long projectId = consumeMapper.getProjectIdByName(project.getProjectName());
                if (projectId == null) {
                    throw new NoSuchElementException("没有该项目：" + project.getProjectName());
                }
                ConsumeItem item = new ConsumeItem();
                item.setConsumeRecordId(recordId);
                item.setProjectId(projectId);
                item.setPrice(project.getPrice());
                item.setRemark(project.getProjectName());
                item.setIsDeleted(false);
                consumeMapper.insertConsumeItem(item);
                Long itemId = item.getId();

                // 3. 遍历 employees，插入 ConsumeService
                for (EmployeeInfo emp : project.getEmployees()) {
                    // 获取员工提成
                    BigDecimal commission = consumeMapper.getCommissionByEmployeeId(emp.getEmployeeId());
                    if (commission == null) {
                        throw new NoSuchElementException("没有该员工，ID：" + emp.getEmployeeId());
                    }
                    ConsumeServiceTable service = new ConsumeServiceTable();
                    service.setConsumeItemId(itemId);
                    service.setEmployeeId(emp.getEmployeeId());
                    service.setEarnings(emp.getIncome());
                    service.setIsDeleted(false);
                    service.setServiceTime(request.getConsumeTime());
                    service.setCommission(commission);
                    // createTime 可数据库自动
                    // commission 如有可计算
                    consumeMapper.insertConsumeService(service);
                }
            }
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("添加消费记录失败", e);
        }
    }
}
