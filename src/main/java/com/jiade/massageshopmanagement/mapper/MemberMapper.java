package com.jiade.massageshopmanagement.mapper;

import com.jiade.massageshopmanagement.model.Member;
import com.jiade.massageshopmanagement.model.MemberRecharge;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
            @Param("minBalance") Double minBalance,
            @Param("maxBalance") Double maxBalance,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
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
            @Param("minBalance") Double minBalance,
            @Param("maxBalance") Double maxBalance,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
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
            @Param("minBalance") Double minBalance,
            @Param("maxBalance") Double maxBalance,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate,
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
            @Param("minBalance") Double minBalance,
            @Param("maxBalance") Double maxBalance,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
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
    int insertRecharge(MemberRecharge recharge);
}
