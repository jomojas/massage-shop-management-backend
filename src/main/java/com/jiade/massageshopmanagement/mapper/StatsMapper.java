package com.jiade.massageshopmanagement.mapper;

import com.jiade.massageshopmanagement.dto.StatsDto.IncomeTrendValueDTO;
import com.jiade.massageshopmanagement.dto.StatsDto.StaffIncomeTrendDataDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface StatsMapper {

    /**
     * 查询收入趋势数据
     * @param start 起始日期
     * @param end 结束日期
     * @param dimension 时间维度（如：day, month）
     * @return 收入趋势数据列表
     */
    List<IncomeTrendValueDTO> selectIncomeTrend(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("dimension") String dimension
    );

    /**
     * 查询最早的支付日期
     * @return 最早的支付日期
     */
    LocalDate selectMinDate();

    /**
     * 查询净收入趋势数据
     * @param start 起始日期
     * @param end 结束日期
     * @param dimension 时间维度（如：day, month）
     * @return 净收入趋势数据列表
     */
    List<IncomeTrendValueDTO> selectExpenseTrend(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end,
            @Param("dimension") String dimension
    );

    // 查询员工收益趋势（一个SQL搞定）
    List<StaffIncomeTrendDataDTO.StaffIncome> selectStaffIncomeTrend(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("period") String period
    );
}
