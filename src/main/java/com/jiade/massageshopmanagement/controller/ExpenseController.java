package com.jiade.massageshopmanagement.controller;

import com.jiade.massageshopmanagement.dto.ApiResponse;
import com.jiade.massageshopmanagement.dto.ExpenseDTO;
import com.jiade.massageshopmanagement.dto.ExpenseListResponse;
import com.jiade.massageshopmanagement.dto.OperationResultDTO;
import com.jiade.massageshopmanagement.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<?> addExpense(@RequestBody ExpenseDTO expenseDTO) {
        try {
            expenseService.addExpense(expenseDTO);
            return ResponseEntity.ok(OperationResultDTO.success());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OperationResultDTO(500, "添加支出信息失败: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> modifyExpense(@PathVariable Long id, @RequestBody ExpenseDTO expenseDTO) {
        try {
            expenseService.modifyExpense(id, expenseDTO);
            return ResponseEntity.ok(OperationResultDTO.success());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OperationResultDTO(500, "修改支出信息失败: " + e.getMessage()));
        }
    }

    @GetMapping("")
    public ApiResponse<ExpenseListResponse> getExpenses(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam(value = "minAmount", required = false) BigDecimal minAmount,
            @RequestParam(value = "maxAmount", required = false) BigDecimal maxAmount,
            @RequestParam(value = "sortBy", defaultValue = "spend_date") String sortBy,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        try {
            // 如果日期都为空，自动设定最近一年
            if (startDate == null && endDate == null) {
                endDate = LocalDate.now();
                startDate = endDate.minusYears(1);
            }
            // 默认参数赋值
            if (sortBy == null || (!sortBy.equals("spend_date") && !sortBy.equals("amount"))) {
                sortBy = "spend_date";
            }
            if (order == null || (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc"))) {
                order = "desc";
            }
            ExpenseListResponse response = expenseService.getExpenses(
                    keyword, category, startDate, endDate, minAmount, maxAmount, sortBy, order, page, size
            );
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取支出记录失败: " + e.getMessage());
        }
    }

    @GetMapping("/deleted")
    public ApiResponse<ExpenseListResponse> getDeletedExpenses(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "startDate", required = false) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) LocalDate endDate,
            @RequestParam(value = "minAmount", required = false) BigDecimal minAmount,
            @RequestParam(value = "maxAmount", required = false) BigDecimal maxAmount,
            @RequestParam(value = "sortBy", defaultValue = "spend_date") String sortBy,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        try {
            // 如果日期都为空，自动设定最近一年
            if (startDate == null && endDate == null) {
                endDate = LocalDate.now();
                startDate = endDate.minusYears(1);
            }
            // 默认参数赋值
            if (sortBy == null || (!sortBy.equals("spend_date") && !sortBy.equals("amount"))) {
                sortBy = "spend_date";
            }
            if (order == null || (!order.equalsIgnoreCase("asc") && !order.equalsIgnoreCase("desc"))) {
                order = "desc";
            }
            ExpenseListResponse response = expenseService.getDeletedExpenses(
                    keyword, category, startDate, endDate, minAmount, maxAmount, sortBy, order, page, size
            );
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取支出记录失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        try {
            expenseService.deleteExpense(id);
            return ResponseEntity.ok(OperationResultDTO.success());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OperationResultDTO(500, "删除支出信息失败: " + e.getMessage()));
        }
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<?> restoreExpense(@PathVariable Long id) {
        try {
            expenseService.restoreExpense(id);
            return ResponseEntity.ok(OperationResultDTO.success());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OperationResultDTO(500, "恢复支出信息失败: " + e.getMessage()));
        }
    }

    @GetMapping("/categories")
    public ApiResponse<List<String>> getExpenseCategories() {
        List<String> categories = expenseService.getExpenseCategories();
        return ApiResponse.success(categories);
    }
}
