package com.jiade.massageshopmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.jiade.massageshopmanagement.service.StaffService;
import com.jiade.massageshopmanagement.dto.ApiResponse;
import com.jiade.massageshopmanagement.dto.StaffServiceRecordResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/staffs")
public class StaffController {

    @Autowired
    private  StaffService staffService;

    @GetMapping("service-records")
    public ApiResponse<StaffServiceRecordResponse> getStaffServiceRecords(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "date_start", required = false) LocalDateTime dateStart,
            @RequestParam(value = "date_end", required = false) LocalDateTime dateEnd,
            @RequestParam(value = "earnings_min", required = false) BigDecimal earningsMin,
            @RequestParam(value = "earnings_max", required = false) BigDecimal earningsMax,
            @RequestParam(value = "sortBy", required = false, defaultValue = "service_date") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "desc") String order,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "page_size", required = false, defaultValue = "10") int pageSize
    ) {
        try {
            // 如果日期都没传，默认查询最近一年
            if (dateStart == null && dateEnd == null) {
                dateEnd = LocalDateTime.now();
                dateStart = dateEnd.minusYears(1);
            }
            // 排序字段白名单
            List<String> allowedSortBy = Arrays.asList("staff_name", "earnings", "service_date");
            if (!allowedSortBy.contains(sortBy)) {
                sortBy = "name";
            }
            // 排序方式白名单
            if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
                order = "ASC";
            }
            StaffServiceRecordResponse response = staffService.queryServiceRecords(
                    keyword, dateStart, dateEnd,
                    earningsMin, earningsMax, sortBy, order, page, pageSize
            );
            return ApiResponse.success(response);
        } catch (Exception e){
          return ApiResponse.error(500, e.getMessage());
        }
    }
}
