package com.jiade.massageshopmanagement.controller;

import com.jiade.massageshopmanagement.dto.ApiResponse;
import com.jiade.massageshopmanagement.dto.LogCategoriesResponse;
import com.jiade.massageshopmanagement.dto.LogQueryResponse;
import com.jiade.massageshopmanagement.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/logs")
public class OperationController {

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping("")
    public ApiResponse<LogQueryResponse> getOperationLogs(
            @RequestParam(value = "operation", required=false) String operation,
            @RequestParam(value = "module", required = false) String module,
            @RequestParam(value = "startTime", required = false) LocalDateTime startTime,
            @RequestParam(value = "endTime", required = false) LocalDateTime endTime,
            @RequestParam(value = "sortBy", defaultValue = "operation_time") String sortBy,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
            ) {
        try {
            // 如果没有传入开始时间和结束时间，默认查询最近一年
            if (startTime == null && endTime == null) {
                endTime = LocalDateTime.now();
                startTime = endTime.minusYears(1);
            }
            // 排序字段白名单
            if (!"operation_time".equals(sortBy))   sortBy = "operation_time";
            // 排序方式白名单
            if (!"asc".equalsIgnoreCase(order) && !"desc".equalsIgnoreCase(order))  order = "desc";

            LogQueryResponse response = operationLogService.getOperationLogs(operation, module, startTime, endTime, sortBy, order, page, size);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取操作日志失败: " + e.getMessage());
        }
    }

    @GetMapping("/categories")
    public ApiResponse<LogCategoriesResponse> getLogCategories() {
        LogCategoriesResponse response = operationLogService.getLogCategories();
        return ApiResponse.success(response);
    }
}
