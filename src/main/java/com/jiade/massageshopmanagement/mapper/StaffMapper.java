package com.jiade.massageshopmanagement.mapper;

import com.jiade.massageshopmanagement.dto.StaffServiceRecordDTO;
import com.jiade.massageshopmanagement.model.Staff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface StaffMapper {

    /**
     * Counts the number of service records based on various filters.
     *
     * @param keyword      optional keyword to search by staff name or phone
     * @param dateStart    optional start date for the service record
     * @param dateEnd      optional end date for the service record
     * @param earningsMin  optional minimum earnings filter
     * @param earningsMax  optional maximum earnings filter
     * @return the count of service records matching the filters
     */
    int countServiceRecords(
            @Param("keyword") String keyword,
            @Param("dateStart") LocalDateTime dateStart,
            @Param("dateEnd") LocalDateTime dateEnd,
            @Param("earningsMin") BigDecimal earningsMin,
            @Param("earningsMax") BigDecimal earningsMax
    );

    /**
     * Retrieves a paginated list of service records based on various filters.
     *
     * @param keyword      optional keyword to search by staff name or phone
     * @param dateStart    optional start date for the service record
     * @param dateEnd      optional end date for the service record
     * @param earningsMin  optional minimum earnings filter
     * @param earningsMax  optional maximum earnings filter
     * @param sortBy       field to sort by (e.g., "date", "earnings")
     * @param order        sort order ("asc" or "desc")
     * @param offset       pagination offset
     * @param pageSize     number of records per page
     * @return a list of service records matching the filters
     */
    List<StaffServiceRecordDTO> selectServiceRecords(
            @Param("keyword") String keyword,
            @Param("dateStart") LocalDateTime dateStart,
            @Param("dateEnd") LocalDateTime dateEnd,
            @Param("earningsMin") BigDecimal earningsMin,
            @Param("earningsMax") BigDecimal earningsMax,
            @Param("sortBy") String sortBy,
            @Param("order") String order,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize
    );

    /**
     * Retrieves a paginated list of staff members based on various filters.
     *
     * @param keyword  optional keyword to search by staff name or phone
     * @param commissionMin  optional minimum commission filter
     * @param commissionMax  optional maximum commission filter
     * @return the number of staff members matching the filters
     */
    int countStaffs(
            @Param("keyword") String keyword,
            @Param("commissionMin") BigDecimal commissionMin,
            @Param("commissionMax") BigDecimal commissionMax
    );

    /**
     * Counts the number of logically deleted staff members based on various filters.
     *
     * @param keyword  optional keyword to search by staff name or phone
     * @param commissionMin  optional minimum commission filter
     * @param commissionMax  optional maximum commission filter
     * @return the count of logically deleted staff members matching the filters
     */
    int countDeletedStaffs(
            @Param("keyword") String keyword,
            @Param("commissionMin") BigDecimal commissionMin,
            @Param("commissionMax") BigDecimal commissionMax
    );

    /**
     * Retrieves a paginated list of staff members based on various filters.
     *
     * @param keyword  optional keyword to search by staff name or phone
     * @param commissionMin  optional minimum commission filter
     * @param commissionMax  optional maximum commission filter
     * @param sortBy   field to sort by (e.g., "name", "commission")
     * @param order    sort order ("asc" or "desc")
     * @param pageSize number of records per page
     * @param offset   pagination offset
     * @return a list of staff members matching the filters
     */
    List<Staff> selectStaffs(
            @Param("keyword") String keyword,
            @Param("commissionMin") BigDecimal commissionMin,
            @Param("commissionMax") BigDecimal commissionMax,
            @Param("sortBy") String sortBy,
            @Param("order") String order,
            @Param("pageSize") int pageSize,
            @Param("offset") int offset
    );

    /**
     * Retrieves a paginated list of logically deleted staff members based on various filters.
     *
     * @param keyword  optional keyword to search by staff name or phone
     * @param commissionMin  optional minimum commission filter
     * @param commissionMax  optional maximum commission filter
     * @param sortBy   field to sort by (e.g., "name", "commission")
     * @param order    sort order ("asc" or "desc")
     * @param pageSize number of records per page
     * @param offset   pagination offset
     * @return a list of logically deleted staff members matching the filters
     */
    List<Staff> selectDeletedStaffs(
            @Param("keyword") String keyword,
            @Param("commissionMin") BigDecimal commissionMin,
            @Param("commissionMax") BigDecimal commissionMax,
            @Param("sortBy") String sortBy,
            @Param("order") String order,
            @Param("pageSize") int pageSize,
            @Param("offset") int offset
    );

    /**
     * Retrieves a list of staff members by their phone number.
     *
     * @param phone the phone number to search for
     * @return a list of staff members with the specified phone number
     */
    List<Staff> selectStaffByPhone(
            @Param("phone") String phone
    );

    /**
     * Retrieves a staff member by their ID.
     *
     * @param id the ID of the staff member
     * @return the staff member with the specified ID, or null if not found
     */
    Staff selectStaffById(@Param("id") Long id);

    /**
     * Retrieves a list of logically deleted staff members based on their name and phone number.
     *
     * @param name  the name of the staff member
     * @param phone the phone number of the staff member
     * @return a list of logically deleted staff members matching the criteria
     */
    List<Staff> selectDeletedStaffByNameAndPhone(@Param("name") String name, @Param("phone") String phone);

    /**
     * Inserts a new staff member into the database.
     *
     * @param staff the staff member to insert
     */
    void insertStaff(@Param("staff") Staff staff);

    /**
     * Updates an existing staff member in the database.
     *
     * @param id    the ID of the staff member to update
     * @param staff the updated staff member data
     */
    void updateStaff(@Param("id") Long id, @Param("staff") Staff staff);

    /**
     * Logically deletes a staff member by setting their status to deleted.
     *
     * @param id the ID of the staff member to delete
     */
    void deleteStaff(@Param("id") Long id);

    /**
     * Restores a logically deleted staff member by setting their status back to active.
     *
     * @param id the ID of the staff member to restore
     */
    void restoreStaff(@Param("id") Long id);
}
