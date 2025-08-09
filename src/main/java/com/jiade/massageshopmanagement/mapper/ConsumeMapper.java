package com.jiade.massageshopmanagement.mapper;

import com.jiade.massageshopmanagement.dto.ConsumptionRecordDetail;
import com.jiade.massageshopmanagement.dto.ConsumeServiceDetail;
import com.jiade.massageshopmanagement.dto.ConsumedProjectInfo;
import com.jiade.massageshopmanagement.model.ConsumeItem;
import com.jiade.massageshopmanagement.model.ConsumeRecord;
import com.jiade.massageshopmanagement.model.ConsumeServiceTable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ConsumeMapper {

    /**
     * Retrieves a paginated list of consumption records based on various filters.
     *
     * @param keyword      optional keyword to search by member name or phone
     * @param minAmount    optional minimum amount filter
     * @param maxAmount    optional maximum amount filter
     * @param startDate    optional start date for consumption
     * @param endDate      optional end date for consumption
     * @param sortBy       field to sort by (e.g., "date", "amount")
     * @param order        sort order ("asc" or "desc")
     * @param offset       pagination offset
     * @param size         number of records per page
     * @return a list of consumption records matching the filters
     */
    List<ConsumptionRecordDetail> selectConsumptionRecordsPage(
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
     * Retrieves a list of consumed project information based on consumption record IDs.
     *
     * @param recordIds a list of consumption record IDs
     * @return a list of consumed project information
     */
    List<ConsumedProjectInfo> selectProjectsByConsumeRecordIds(
            @Param("recordIds") List<Long> recordIds
    );

    /**
     * Retrieves a list of service details (employees) based on consumed item IDs.
     *
     * @param itemIds a list of consumed item IDs
     * @return a list of service details for the specified item IDs
     */
    List<ConsumeServiceDetail> selectEmployeesByConsumeItemIds(
            @Param("itemIds") List<Long> itemIds
    );

    /**
     * Counts the number of consumption records based on various filters.
     *
     * @param keyword      optional keyword to search by member name or phone
     * @param minAmount    optional minimum amount filter
     * @param maxAmount    optional maximum amount filter
     * @param startDate    optional start date for consumption
     * @param endDate      optional end date for consumption
     * @return the count of consumption records matching the filters
     */
    int countConsumptionRecords(
            @Param("keyword") String keyword,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Inserts a new consumption record into the database.
     *
     * @param record the consumption record to insert
     */
    void insertConsumeRecord(ConsumeRecord record);

    /**
     * Inserts a new consume item into the database.
     *
     * @param item the consume item to insert
     */
    void insertConsumeItem(ConsumeItem item);

    /**
     * Inserts a new consumer service into the database.
     *
     * @param service the consume service to insert
     */
    void insertConsumeService(ConsumeServiceTable service);

    /**
     * Retrieves the project ID by its name.
     *
     * @param projectName the name of the project
     * @return the ID of the project, or null if not found
     */
    Long getProjectIdByName(
            @Param("projectName") String projectName
    );

    /**
     * Retrieves the commission amount for a specific employee by their ID.
     *
     * @param employeeId the ID of the employee
     * @return the commission amount for the employee, or null if not found
     */
    BigDecimal getCommissionByEmployeeId(
            @Param("employeeId") Long employeeId
    );
}
