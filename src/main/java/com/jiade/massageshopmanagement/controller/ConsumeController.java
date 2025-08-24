package com.jiade.massageshopmanagement.controller;

import com.jiade.massageshopmanagement.dto.ApiResponse;
import com.jiade.massageshopmanagement.dto.ConsumeListData;
import com.jiade.massageshopmanagement.dto.ConsumeRecordRequest;
import com.jiade.massageshopmanagement.dto.ConsumeRecordUpdateRequest;
import com.jiade.massageshopmanagement.dto.OperationResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.jiade.massageshopmanagement.service.ConsumeService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.math.BigDecimal;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/consumptions")
public class ConsumeController {
    @Autowired
    private ConsumeService consumeService;

    /**
     * 获取消费记录列表
     * @param keyword 关键词搜索
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param minAmount 最小金额
     * @param maxAmount 最大金额
     * @param sortBy 排序字段
     * @param order 排序方式
     * @param page 页码
     * @param size 每页大小
     * @return 消费记录列表数据
     */
    @GetMapping("")
    public ApiResponse<ConsumeListData> getConsumeRecords(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(defaultValue = "consume_time") String sortBy,
            @RequestParam(defaultValue = "desc") String order,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        // 如果日期都没传，默认查询最近一年
        if (startDate == null && endDate == null) {
            endDate = LocalDateTime.now();
            startDate = endDate.minusYears(1);
        }
        // 排序字段白名单
        List<String> allowedSortBy = Arrays.asList("name", "total_price", "consume_time");
        if (!allowedSortBy.contains(sortBy)) {
            sortBy = "consume_time";
        }
        // 排序方式白名单
        if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
            order = "DESC";
        }
        ConsumeListData response = consumeService.getConsumeRecords(keyword, startDate, endDate, minAmount, maxAmount, sortBy, order, page, size);
        return ApiResponse.success(response);
    }

    /**
     * 获取消费记录详情
     * @param request 要添加的消费记录
     * @return 消费记录详情
     */
    @PostMapping("")
    public ResponseEntity<?> addConsumeRecord(@RequestBody ConsumeRecordRequest request) {
        try {
            consumeService.addConsumeRecord(request);
            return ResponseEntity.ok(OperationResultDTO.success());
        } catch (NoSuchElementException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OperationResultDTO(500, e.getMessage()));
        }
    }

    /**
     * 更新消费记录
     * @param id 消费记录ID
     * @param request 更新请求
     * @return 操作结果
     */
    @PutMapping("/{id}")
    public  ResponseEntity<?> updateConsumeRecord(@PathVariable Long id, @RequestBody ConsumeRecordUpdateRequest request) {
        try {
            consumeService.updateConsumeRecord(id, request);
            return ResponseEntity.ok(OperationResultDTO.success());
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch (NoSuchElementException e) {
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
