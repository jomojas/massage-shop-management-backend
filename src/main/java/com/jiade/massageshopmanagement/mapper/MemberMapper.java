package com.jiade.massageshopmanagement.mapper;

import com.jiade.massageshopmanagement.dto.MemberModifyRequest;
import com.jiade.massageshopmanagement.model.Member;
import com.jiade.massageshopmanagement.model.MemberRecharge;
import com.jiade.massageshopmanagement.model.RechargeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MemberMapper {

    /**
     * Retrieves a paginated list of members based on various filters.
     * @param keyword
     * @param minBalance
     * @param maxBalance
     * @param startDate
     * @param endDate
     * @param sortBy
     * @param order
     * @param offset
     * @param size
     * @return a list of members matching the filters
     */
    List<Member> selectMembersByFilters(
            @Param("keyword") String keyword,
            @Param("minBalance") BigDecimal minBalance,
            @Param("maxBalance") BigDecimal maxBalance,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("sortBy") String sortBy,
            @Param("order") String order,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * Counts the number of members based on various filters.
     *
     * @param keyword      optional keyword to search by name or phone
     * @param minBalance   optional minimum balance filter
     * @param maxBalance   optional maximum balance filter
     * @param startDate    optional start date for member creation
     * @param endDate      optional end date for member creation
     * @return the count of members matching the filters
     */
    int countMembersByFilters(
            @Param("keyword") String keyword,
            @Param("minBalance") BigDecimal minBalance,
            @Param("maxBalance") BigDecimal maxBalance,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Retrieves a paginated list of logically deleted members based on various filters.
     *
     * @param keyword      optional keyword to search by name or phone
     * @param minBalance   optional minimum balance filter
     * @param maxBalance   optional maximum balance filter
     * @param startDate    optional start date for member creation
     * @param endDate      optional end date for member creation
     * @param sortBy       the field to sort by (default is "name")
     * @param order        the order of sorting (default is "asc")
     * @param offset       the offset for pagination
     * @param size         the number of members per page
     * @return a list of logically deleted members matching the filters
     */
    List<Member> selectDeletedMembersByFilters(
            @Param("keyword") String keyword,
            @Param("minBalance") BigDecimal minBalance,
            @Param("maxBalance") BigDecimal maxBalance,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("sortBy") String sortBy,
            @Param("order") String order,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * Counts the number of logically deleted members based on various filters.
     *
     * @param keyword      optional keyword to search by name or phone
     * @param minBalance   optional minimum balance filter
     * @param maxBalance   optional maximum balance filter
     * @param startDate    optional start date for member creation
     * @param endDate      optional end date for member creation
     * @return the count of logically deleted members matching the filters
     */
    int countDeletedMembersByFilters(
            @Param("keyword") String keyword,
            @Param("minBalance") BigDecimal minBalance,
            @Param("maxBalance") BigDecimal maxBalance,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Logically deletes a member by setting isDeleted to 1.
     *
     * @param memberId the ID of the member to delete
     * @return the number of rows affected
     */
    int logicalDeleteMember(@Param("memberId") Long memberId);

    /**
     * Restores a logically deleted member by setting isDeleted to 0.
     *
     * @param memberId the ID of the member to restore
     * @return the number of rows affected
     */
    int logicalRestoreMember(@Param("memberId") Long memberId);

    /**
     * Inserts a new member into the database.
     *
     * @param member the member to insert
     */
    void insertMember(Member member);

    /**
     * Insert a new member recharge record into the database.
     *
     * @param recharge
     * @return the number of rows affected
     */
    void insertRecharge(MemberRecharge recharge);

    /**
     * Updates the balance of a member.
     *
     * @param memberId the ID of the member
     * @param amount   the amount to update the balance by (can only be positive)
     */
    int updateMemberBalance(@Param("memberId") Long memberId, @Param("amount") BigDecimal amount);

    /**
     * Updates the info of a member
     *
     * @param memberId the ID of the member
     * @param member   the new info of the member
     */
    int updateMemberInfo(@Param("memberId") Long memberId, Member member);

    /**
     * Retrieves a paginated list of recharge records based on various filters.
     *
     * @param keyword      optional keyword to search by name or phone
     * @param minAmount   optional minimum amount filter
     * @param maxAmount   optional maximum amount filter
     * @param startDate    optional start date for member creation
     * @param endDate      optional end date for member creation
     * @param sortBy       the field to sort by (default is "name")
     * @param order        the order of sorting (default is "asc")
     * @param offset       the offset for pagination
     * @param size         the number of members per page
     * @return a list of logically deleted members matching the filters
     */
    List<RechargeRecord> selectRechargeRecordsByFilters(
            @Param("keyword") String keyword,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("sortBy") String sortBy,
            @Param("order") String order,
            @Param("offset") int offset,
            @Param("size") int size
    );

    /**
     * Counts the number of recharge records based on various filters.
     *
     * @param keyword      optional keyword to search by name or phone
     * @param minAmount   optional minimum Amount filter
     * @param maxAmount   optional maximum Amount filter
     * @param startDate    optional start date for member creation
     * @param endDate      optional end date for member creation
     * @return the count of logically deleted members matching the filters
     */
    int countRechargeRecordsByFilters(
            @Param("keyword") String keyword,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Modifies a recharge record by its ID.
     *
     * @param recordId the ID of the recharge record to modify
     * @param recharge the new info of the recharge record
     * @return the number of rows affected
     */
    int modifyRechargeRecord(@Param("recordId") Long recordId, RechargeRecord recharge);
}
