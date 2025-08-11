package com.jiade.massageshopmanagement.mapper;

import com.jiade.massageshopmanagement.dto.StaffServiceRecordDTO;
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
}
