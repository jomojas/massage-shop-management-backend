package com.jiade.massageshopmanagement.controller;

import com.jiade.massageshopmanagement.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.jiade.massageshopmanagement.service.StaffService;

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

    @GetMapping("")
    public ApiResponse<StaffListResponse> getStaffList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "commission_min", required = false) BigDecimal commissionMin,
            @RequestParam(value = "commission_max", required = false) BigDecimal commissionMax,
            @RequestParam(value = "sortBy", required = false, defaultValue = "name") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "page_size", required = false, defaultValue = "10") int pageSize
    ) {
        try {
            // 排序字段白名单
            List<String> allowedSortBy = Arrays.asList("name", "commission");
            if (!allowedSortBy.contains(sortBy)) {
                sortBy = "name";
            }
            // 排序方式白名单
            if (!"asc".equalsIgnoreCase(order) && !"desc".equalsIgnoreCase(order)) {
                order = "asc";
            }
            StaffListResponse response = staffService.queryStaffList(
                    keyword, commissionMin, commissionMax, sortBy, order, page, pageSize
            );
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @GetMapping("/deleted")
    public ApiResponse<StaffListResponse> getDeletedStaffList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "commission_min", required = false) BigDecimal commissionMin,
            @RequestParam(value = "commission_max", required = false) BigDecimal commissionMax,
            @RequestParam(value = "sortBy", required = false, defaultValue = "name") String sortBy,
            @RequestParam(value = "order", required = false, defaultValue = "asc") String order,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            @RequestParam(value = "page_size", required = false, defaultValue = "10") int pageSize
    ) {
        try {
            // 排序字段白名单
            List<String> allowedSortBy = Arrays.asList("name", "commission");
            if (!allowedSortBy.contains(sortBy)) {
                sortBy = "name";
            }
            // 排序方式白名单
            if (!"asc".equalsIgnoreCase(order) && !"desc".equalsIgnoreCase(order)) {
                order = "asc";
            }
            StaffListResponse response = staffService.queryDeletedStaffList(
                    keyword, commissionMin, commissionMax, sortBy, order, page, pageSize
            );
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }


    @PostMapping("")
    public ResponseEntity<?> addStaff(@RequestBody StaffAddRequest request) {
        try {
            BigDecimal commission = request.getCommission();
            if (commission == null
                    || commission.compareTo(BigDecimal.ZERO) < 0
                    || commission.compareTo(BigDecimal.ONE) > 0)
            {
                throw new IllegalArgumentException("提成比例必须大于等于 0 且小于等于 1");
            }
            staffService.addStaff(request);
            return ResponseEntity.ok(OperationResultDTO.success());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OperationResultDTO(500, e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStaff(
            @PathVariable("id") Long id,
            @RequestBody StaffUpdateRequest request
    ) {
        try {
            // 校验 commission 合法性
            BigDecimal commission = request.getCommission();
            if (commission == null
                    || commission.compareTo(BigDecimal.ZERO) < 0
                    || commission.compareTo(BigDecimal.ONE) > 0) {
                throw new IllegalArgumentException("提成比例必须大于等于 0 且小于等于 1");
            }

            staffService.updateStaff(id, request);
            return ResponseEntity.ok(new OperationResultDTO(200, "success"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OperationResultDTO(500, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable("id") Long id) {
        try {
            staffService.deleteStaff(id);
            return ResponseEntity.ok(new OperationResultDTO(200, "success"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OperationResultDTO(500, e.getMessage()));
        }
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<?> restoreStaff(@PathVariable("id") Long id) {
        try {
            staffService.restoreStaff(id);
            return ResponseEntity.ok(new OperationResultDTO(200, "success"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OperationResultDTO(500, e.getMessage()));
        }
    }
}
