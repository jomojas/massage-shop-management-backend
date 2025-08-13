package com.jiade.massageshopmanagement.service;

import com.jiade.massageshopmanagement.dto.EnumItem;
import com.jiade.massageshopmanagement.dto.LogCategoriesResponse;
import com.jiade.massageshopmanagement.dto.LogQueryResponse;
import com.jiade.massageshopmanagement.enums.OperationModule;
import com.jiade.massageshopmanagement.enums.OperationType;
import com.jiade.massageshopmanagement.mapper.OperationLogMapper;
import com.jiade.massageshopmanagement.model.OperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OperationLogService {
    @Autowired
    private OperationLogMapper operationLogMapper;

    public void recordLog(OperationType operation, OperationModule module, String detail) {
        OperationLog log = new OperationLog();
        log.setOperation(operation);
        log.setModule(module);
        log.setDetail(detail);
        operationLogMapper.insert(log);
    }

    public LogQueryResponse getOperationLogs(String operation, String module, LocalDateTime startTime, LocalDateTime endTime, String sortBy, String order, int page, int size) {
        try {
            // 1. 计算分页
            int offset = (page - 1) * size;
            // 2. 查询总数
            long totalLogs = operationLogMapper.countLogs(operation, module, startTime, endTime);
            // 3. 查询数据列表
            List<OperationLog> logList = operationLogMapper.selectLogs(operation, module, startTime, endTime, sortBy, order, offset, size);
            // 4. 计算总页数
            int totalPages = (int) Math.ceil((double) totalLogs / size);
            // 5. 封装响应对象
            return new LogQueryResponse(logList, totalLogs, totalPages, page);
        } catch (Exception e) {
            // 处理异常，记录日志等
            throw new RuntimeException("查询操作日志失败: " + e.getMessage());
        }
    }

    public LogCategoriesResponse getLogCategories() {
        try {
            List<EnumItem> operationTypes = Arrays.stream(OperationType.values())
                    .map(e -> new EnumItem(e.name(), e.getDescription()))
                    .collect(Collectors.toList());

            List<EnumItem> moduleTypes = Arrays.stream(OperationModule.values())
                    .map(e -> new EnumItem(e.name(), e.getDescription()))
                    .collect(Collectors.toList());

            return new LogCategoriesResponse(operationTypes, moduleTypes);
        } catch (Exception e) {
            // 处理异常，记录日志等
            throw new RuntimeException("查询操作日志类别失败: " + e.getMessage());
        }
    }
}
