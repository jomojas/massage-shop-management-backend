package com.jiade.massageshopmanagement.service;

import com.jiade.massageshopmanagement.dto.*;
import com.jiade.massageshopmanagement.enums.OperationModule;
import com.jiade.massageshopmanagement.enums.OperationType;
import com.jiade.massageshopmanagement.model.ConsumeItem;
import com.jiade.massageshopmanagement.model.ConsumeRecord;
import com.jiade.massageshopmanagement.model.ConsumeServiceTable;
import com.jiade.massageshopmanagement.model.Member;
import com.jiade.massageshopmanagement.sms.SmsService;
import com.jiade.massageshopmanagement.sms.SmsTemplateId;
import org.hibernate.validator.constraintvalidators.RegexpURLValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jiade.massageshopmanagement.mapper.ConsumeMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
public class ConsumeService {
    @Autowired
    private ConsumeMapper consumeMapper;
    @Autowired
    private OperationLogService operationLogService;
    @Autowired
    private SmsService smsService;

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
            validateConsumeRecord(request);

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

            // 日志记录
            String projectNames = request.getProjects().stream()
                    .map(ProjectInfo::getProjectName)
                    .collect(Collectors.joining(","));

            String consumerInfo;
            if (request.getMemberId() != null) {
                Member member = consumeMapper.selectById(request.getMemberId());
                if (member != null) {
                    consumerInfo = String.format("会员ID：%s，姓名：%s，电话：%s", member.getId(), member.getName(), member.getPhone());
                } else {
                    consumerInfo = String.format("会员ID：%s", request.getMemberId());
                }
            } else {
                consumerInfo = String.format("客户信息：%s", request.getCustomerDesc());
            }

            String logDetail = String.format(
                    "新增消费记录，%s, 总金额：%s，项目：%s，描述：%s",
                    consumerInfo,
                    request.getTotalPrice(),
                    projectNames,
                    request.getRecordDetail()
            );

            operationLogService.recordLog(
                    OperationType.CREATE,
                    OperationModule.CONSUMPTION,
                    logDetail
            );

            // 发送短信通知
            if (request.getMemberId() != null) {
                Member member = consumeMapper.selectById(request.getMemberId());
                if (member != null && member.getPhone() != null && !member.getPhone().isEmpty()) {
                    String items = request.getProjects().stream()
                            .map(p -> p.getProjectName() + p.getPrice() + "元")
                            .collect(Collectors.joining("，"));

                    Map<String, String> smsParams = new HashMap<>();
                    smsParams.put("name", member.getName());
                    smsParams.put("total", request.getTotalPrice().toString());
                    smsParams.put("items", items);

                    smsService.send(member.getPhone(), SmsTemplateId.MEMBER_CONSUME, smsParams);
                }
            }
        } catch (IllegalArgumentException e) {
            throw e; // 直接抛出参数错误
        } catch (NoSuchElementException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("添加消费记录失败" + e.getMessage(), e);
        }
    }

    @Transactional
    public void updateConsumeRecord(Long recordId, ConsumeRecordUpdateRequest request) {
        try {
            // 校验参数
            boolean isMember = !isEmpty(request.getName()) && !isEmpty(request.getPhone());
            boolean isGuest = !isEmpty(request.getDescription());
            if ((isMember && isGuest) || (!isMember && !isGuest)) {
                throw new IllegalArgumentException("消费人信息填写错误：只能填写会员信息（姓名、电话）或普通客户说明（description），不能同时填写或都不填。");
            }

            // 校验total_price和项目价格之和， 以及每个项目的员工收益分摊
            validateUpdateConsumeRecord(request);

            // 组装主表
            ConsumeRecord record = new ConsumeRecord();
            record.setId(recordId);
            record.setTotalPrice(request.getTotalPrice());
            record.setConsumeTime(request.getConsumeTime());
            record.setRecordDetail(request.getRecordDetail());

            if (isMember) {
                // 通过 userName 查 memberId
                Long memberId = consumeMapper.getMemberIdByPhone(request.getPhone());
                if (memberId == null) {
                    throw new NoSuchElementException("会员不存在：" + request.getName());
                }
                record.setUserId(memberId);
                record.setUserType("MEMBER");
            } else {
                record.setUserId(null);
                record.setUserType("GUEST");
                record.setDescription(request.getDescription());
            }

            // 更新主表
            consumeMapper.updateConsumeRecord(record);

            // 2. 删除原有明细和服务
            List<Long> oldItemIds = consumeMapper.selectConsumeItemIdsByRecordId(recordId);
            if (!oldItemIds.isEmpty()) {
                consumeMapper.deleteConsumeServicesByItemIds(oldItemIds);
                consumeMapper.deleteConsumeItemsByRecordId(recordId);
            }

            // 3. 新增明细和服务
            for (ProjectUpdateInfo project : request.getProjects()) {
                // 获取项目ID，判空
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

                // 员工服务
                for (EmployeeRef emp : project.getEmployees()) {
                    Long employeeId = emp.getId();
                    BigDecimal income = emp.getIncome();
                    if (employeeId == null || income == null) {
                        throw new IllegalArgumentException("员工ID和收益不能为空，项目：" + project.getProjectName());
                    }
                    BigDecimal commission = consumeMapper.getCommissionByEmployeeId(employeeId);
                    if (commission == null) {
                        throw new NoSuchElementException("没有该员工：" + emp.getName());
                    }
                    ConsumeServiceTable service = new ConsumeServiceTable();
                    service.setConsumeItemId(itemId);
                    service.setEmployeeId(employeeId);
                    service.setEarnings(income); // 或者按业务逻辑分配
                    service.setIsDeleted(false);
                    service.setServiceTime(request.getConsumeTime());
                    service.setCommission(commission);
                    consumeMapper.insertConsumeService(service);
                }
            }

            // 日志记录
            String projectNames = request.getProjects().stream()
                    .map(ProjectUpdateInfo::getProjectName)
                    .collect(Collectors.joining(","));
            String customerInfo;
            if (!isEmpty(request.getName()) && !isEmpty(request.getPhone())) {
                customerInfo = String.format("会员ID：%s，姓名：%s，电话：%s", recordId, request.getName(), request.getPhone());
            } else {
                customerInfo = String.format("客户说明：%s", request.getDescription());
            }

            String logDetail = String.format(
                    "更新消费记录，%s，总金额：%s，项目：%s，描述：%s",
                    customerInfo,
                    request.getTotalPrice(),
                    projectNames,
                    request.getRecordDetail()
            );

            operationLogService.recordLog(
                    OperationType.UPDATE,
                    OperationModule.CONSUMPTION,
                    logDetail
            );
        } catch (IllegalArgumentException e) {
            throw e; // 直接抛出参数错误
        } catch (NoSuchElementException e) {
            throw e; // 直接抛出未找到错误
        } catch (Exception e) {
            throw new RuntimeException("更新消费记录失败", e);
        }
    }

    // 工具方法
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private void validateUpdateConsumeRecord(ConsumeRecordUpdateRequest request) {
        // 校验总价
        BigDecimal totalProjectPrice = request.getProjects().stream()
                .map(ProjectUpdateInfo::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (request.getTotalPrice().compareTo(totalProjectPrice) != 0) {
            throw new IllegalArgumentException("总价与项目价格之和不一致！");
        }

        // 校验每个项目员工收益分摊
        for (ProjectUpdateInfo project : request.getProjects()) {
            BigDecimal projectIncomeSum = project.getEmployees().stream()
                    .map(EmployeeRef::getIncome)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (project.getPrice().compareTo(projectIncomeSum) != 0) {
                throw new IllegalArgumentException("项目[" + project.getProjectName() + "]的价格与员工收益之和不一致！");
            }
        }
    }

    private void validateConsumeRecord(ConsumeRecordRequest request) {
        // 校验总价
        BigDecimal totalProjectPrice = request.getProjects().stream()
                .map(ProjectInfo::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (request.getTotalPrice().compareTo(totalProjectPrice) != 0) {
            throw new IllegalArgumentException("总价与项目价格之和不一致！");
        }

        // 校验每个项目员工收益分摊
        for (ProjectInfo project : request.getProjects()) {
            BigDecimal projectIncomeSum = project.getEmployees().stream()
                    .map(EmployeeInfo::getIncome)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            if (project.getPrice().compareTo(projectIncomeSum) != 0) {
                throw new IllegalArgumentException("项目[" + project.getProjectName() + "]的价格与员工收益之和不一致！");
            }
        }
    }
}
