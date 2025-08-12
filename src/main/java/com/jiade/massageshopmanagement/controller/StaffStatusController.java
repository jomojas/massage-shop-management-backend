package com.jiade.massageshopmanagement.controller;

import com.jiade.massageshopmanagement.dto.*;
import com.jiade.massageshopmanagement.service.StaffStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/staff-status")
public class StaffStatusController {

    @Autowired
    private StaffStatusService staffStatusService;

    @PostMapping("/{id}")
    public ResponseEntity<?> addStaffStatus(
            @PathVariable("id") Long id,
            @RequestBody StaffStatusRequest request
    ) {
        try {
            staffStatusService.addStaffStatus(id, request);
            return ResponseEntity.ok(new OperationResultDTO(200, "success"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OperationResultDTO(500, "添加员工状态失败"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStaffStatus(
            @PathVariable("id") Long id,
            @RequestBody StaffStatusRequest request
    ) {
        try {
            staffStatusService.updateStaffStatus(id, request);
            return ResponseEntity.ok(new OperationResultDTO(200, "success"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OperationResultDTO(500, "更新员工状态失败" + e.getMessage()));
        }
    }

    @GetMapping("/categories")
    public ApiResponse<List<String>> getStaffStatusCategories() {
        List<String> categories = staffStatusService.getStaffStatusCategories();
        return ApiResponse.success(categories);
    }

    @GetMapping("")
    public ApiResponse<StaffStatusResponse> getStaffStatusList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            // 如果日期都为空，自动设定最近一年
            if (startDate == null && endDate == null) {
                endDate = LocalDate.now();
                startDate = endDate.minusYears(1);
            }
            // 默认参数赋值
            if (sortBy == null || (!sortBy.equals("staff_name") && !sortBy.equals("date"))) {
                sortBy = "date";
            }
            if (order == null || (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc"))) {
                order = "desc";
            }
            StaffStatusResponse record = staffStatusService.getStaffStatusList(
                    keyword, status, startDate, endDate, sortBy, order, page, size
            );
            return ApiResponse.success(record);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "获取员工状态列表失败: " + e.getMessage());
        }
    }
}
