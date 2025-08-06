package com.jiade.massageshopmanagement.controller;

import com.jiade.massageshopmanagement.dto.MemberDTO;
import com.jiade.massageshopmanagement.dto.MemberAddRequest;
import com.jiade.massageshopmanagement.dto.MemberListResponse;
import com.jiade.massageshopmanagement.dto.OperationResultDTO;
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
    public MemberListResponse getMembers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minBalance,
            @RequestParam(required = false) Double maxBalance,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order
    ) {
        return memberService.getMembers(keyword, minBalance, maxBalance, startDate, endDate, page, size, sortBy, order);
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
    public MemberListResponse getDeletedMembers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minBalance,
            @RequestParam(required = false) Double maxBalance,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "name") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String order
    ) {
        return memberService.getDeletedMembers(keyword, minBalance, maxBalance, startDate, endDate, page, size, sortBy, order);
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
            MemberDTO newMember = memberService.addMemberWithRecharge(request);
            return ResponseEntity.ok(newMember);
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new OperationResultDTO(400, e.getMessage()));
        }
    }
}
