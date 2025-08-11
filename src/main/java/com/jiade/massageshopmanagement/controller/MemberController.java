package com.jiade.massageshopmanagement.controller;

import com.jiade.massageshopmanagement.dto.*;
import com.jiade.massageshopmanagement.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    /**
     * Retrieves a paginated list of members based on various filters.
     *
     * @param keyword      optional keyword to search by name or phone
     * @param minBalance   optional minimum balance filter
     * @param maxBalance   optional maximum balance filter
     * @param startDate    optional start date for member creation
     * @param endDate      optional end date for member creation
     * @param page         the page number to retrieve (default is 1)
     * @param size         the number of members per page (default is 10)
     * @param sortBy       the field to sort by (default is "name")
     * @param order        the order of sorting (default is "asc")
     * @return a MemberListResponse containing the list of members and pagination info
     */
    @GetMapping
    public ApiResponse<MemberListResponse> getMembers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minBalance,
            @RequestParam(required = false) BigDecimal maxBalance,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order
    ) {
        // 排序字段白名单
        List<String> allowedSortBy = Arrays.asList("name", "balance", "created_time");
        if (!allowedSortBy.contains(sortBy)) {
            sortBy = "name";
        }
        // 排序方式白名单
        if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
            order = "ASC";
        }
        MemberListResponse response = memberService.getMembers(keyword, minBalance, maxBalance, startDate, endDate, page, size, sortBy, order);
        return ApiResponse.success(response);
    }

    /**
     * Retrieves a paginated list of logically deleted members based on various filters.
     *
     * @param keyword      optional keyword to search by name or phone
     * @param minBalance   optional minimum balance filter
     * @param maxBalance   optional maximum balance filter
     * @param startDate    optional start date for member creation
     * @param endDate      optional end date for member creation
     * @param page         the page number to retrieve (default is 1)
     * @param size         the number of members per page (default is 10)
     * @param sortBy       the field to sort by (default is "name")
     * @param order        the order of sorting (default is "asc")
     * @return a MemberListResponse containing the list of logically deleted members and pagination info
     */
    @GetMapping("/deleted")
    public ApiResponse<MemberListResponse> getDeletedMembers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minBalance,
            @RequestParam(required = false) BigDecimal maxBalance,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order
    ) {
        // 排序字段白名单
        List<String> allowedSortBy = Arrays.asList("name", "balance", "created_time");
        if (!allowedSortBy.contains(sortBy)) {
            sortBy = "name";
        }
        // 排序方式白名单
        if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
            order = "ASC";
        }
        MemberListResponse response = memberService.getDeletedMembers(keyword, minBalance, maxBalance, startDate, endDate, page, size, sortBy, order);
        return ApiResponse.success(response);
    }

    /**
     * Logically deletes a member by setting the isDeleted flag to true.
     *
     * @param id the ID of the member to be logically deleted
     * @return a ResponseEntity indicating the result of the operation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        try {
            memberService.deleteMemberLogically(id);
            return ResponseEntity.ok(OperationResultDTO.success());
        } catch (Exception e) {
            // e.getCause().getMessage() will return the inner exception message if available
            // e.getMessage() will return the outer exception message
            String errorMsg = (e.getCause() != null && e.getCause().getMessage() != null)
                ? e.getCause().getMessage()
                : e.getMessage();
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new OperationResultDTO(500, errorMsg));
        }
    }

    /**
     * Restores a logically deleted member by setting the isDeleted flag to false.
     *
     * @param id the ID of the member to be restored
     * @return a ResponseEntity indicating the result of the operation
     */
    @PutMapping("/{id}/restore")
    public ResponseEntity<?> restoreMember(@PathVariable Long id) {
        try {
            memberService.restoreMemberLogically(id);
            return ResponseEntity.ok(OperationResultDTO.success());
        } catch (Exception e) {
            // e.getCause().getMessage() will return the inner exception message if available
            // e.getMessage() will return the outer exception message
            String errorMsg = (e.getCause() != null && e.getCause().getMessage() != null)
                ? e.getCause().getMessage()
                : e.getMessage();
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new OperationResultDTO(500, errorMsg));
        }
    }

    /**
     * Adds a new member with an initial recharge.
     *
     * @param request the request containing member details and initial recharge amount
     * @return a ResponseEntity containing the newly added member information
     */
    @PostMapping
    public ResponseEntity<?> addMember(@RequestBody MemberAddRequest request) {
        try {
            memberService.addMemberWithRecharge(request);
            return ResponseEntity.ok(OperationResultDTO.success());
        } catch (IllegalArgumentException e) {
            // Validation error, return 400 with message
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
     * Add a recharge record for a member
     * @param memberId
     * @param request the request containing recharged amount and remark
     * @return
     */
    @PostMapping("/{id}/recharges")
    public ResponseEntity<?> rechargeMember(@PathVariable("id") Long memberId, @RequestBody RechargeRequestDTO request) {
        try {
            memberService.rechargeOfMember(memberId, request);
            return  ResponseEntity.ok(OperationResultDTO.success());
        } catch (IllegalArgumentException e) {
            // Validation error, return 400 with message
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch(Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new OperationResultDTO(500, e.getMessage()));
        }
    }

    /**
     * Modify member info
     * @param memberId
     * @param request the request containing new member info
     * @return
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> modifyMember(@PathVariable("id") Long memberId, @RequestBody MemberModifyRequest request) {
        try {
            memberService.modifyMemberInfo(memberId, request);
            return  ResponseEntity.ok(OperationResultDTO.success());
        } catch (IllegalArgumentException e) {
            // Validation error, return 400 with message
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch(Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new OperationResultDTO(500, e.getMessage()));
        }
    }

    /**
     * Retrieves recharge records along with member id, name and phone.
     *
     * @param keyword   optional keyword to search by member name or phone
     * @param minAmount optional minimum recharge amount filter
     * @param maxAmount optional maximum recharge amount filter
     * @param startDate optional recharge start date (format: yyyy-MM-dd)
     * @param endDate   optional recharge end date (format: yyyy-MM-dd)
     * @param page      the page number to retrieve (default is 1)
     * @param size      the number of records per page (default is 10)
     * @param sortBy    the field to sort by (default is recharge_time)
     * @param order     the order of sorting (default is desc)
     * @return a RechargeRecordResponse containing records and pagination details
     */
    @GetMapping("/recharges")
    public ApiResponse<RechargeRecordResponse> getRechargeRecords(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "recharge_time") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String order
    ) {
        // 如果日期都没传，默认查询最近一年
        if (startDate == null && endDate == null) {
            endDate = LocalDateTime.now();
            startDate = endDate.minusYears(1);
        }
        // 排序字段白名单
        List<String> allowedSortBy = Arrays.asList("name", "amount", "recharge_time");
        if (!allowedSortBy.contains(sortBy)) {
            sortBy = "recharge_time";
        }
        // 排序方式白名单
        if (!"ASC".equalsIgnoreCase(order) && !"DESC".equalsIgnoreCase(order)) {
            order = "DESC";
        }
        RechargeRecordResponse response = memberService.getRechargeRecords(keyword, minAmount, maxAmount, startDate, endDate, page, size, sortBy, order);
        return ApiResponse.success(response);
    }

    /**
     * Modifies a recharge record by its ID.
     *
     * @param recordId the ID of the recharge record to be modified
     * @param request  the request containing new recharge details
     * @return a ResponseEntity indicating the result of the operation
     */
    @PutMapping("/recharges/{recordId}")
    public ResponseEntity<?> modifyRechargeRecord(@PathVariable("recordId") Long recordId, @RequestBody RechargeModifyRequest request) {
        try {
            memberService.modifyRechargeRecord(recordId, request);
            return ResponseEntity.ok(OperationResultDTO.success());
        } catch (IllegalArgumentException e) {
            // Validation error, return 400 with message
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new OperationResultDTO(400, e.getMessage()));
        } catch(Exception e) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new OperationResultDTO(500, e.getMessage()));
        }
    }
}
