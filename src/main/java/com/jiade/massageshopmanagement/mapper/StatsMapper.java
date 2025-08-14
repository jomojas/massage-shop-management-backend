package com.jiade.massageshopmanagement.mapper;

import com.jiade.massageshopmanagement.dto.StatsDto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    /**
     * 查询消费占比数据
     * @param start 起始日期
     * @param end 结束日期
     * @return 消费占比数据列表
     */
    List<ConsumptionRatioDTO> selectConsumptionRatio(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    /**
     * 查询项目收入占比数据
     * @param start 起始日期
     * @param end 结束日期
     * @return 项目收入占比数据列表
     */
    List<ProjectIncomeRatioDTO> selectProjectIncomeRatio(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    /**
     * 查询统计摘要数据
     * @param start 起始日期
     * @param end 结束日期
     * @return 统计摘要数据
     */
    BigDecimal selectTotalIncome(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    /**
     * 查询统计摘要数据
     * @param start 起始日期
     * @param end 结束日期
     * @return 统计摘要数据
     */
    BigDecimal selectTotalExpense(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    /**
     * 查询会员消费数据
     * @param memberId 会员ID
     * @param start 起始日期
     * @param end 结束日期
     * @return 会员消费数据列表
     */
    List<MemberConsumptionDTO> selectMemberConsumption(
            @Param("memberId") Long memberId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    /**
     * 查询员工服务数据
     * @param staffId 员工ID
     * @param start 起始日期
     * @param end 结束日期
     * @return 员工服务数据列表
     */
    List<StaffServiceDTO> selectStaffService(
            @Param("staffId") Long staffId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    /**
     * 查询员工薪资数据
     * @param yearStart 年份起始日期
     * @param yearEnd 年份结束日期
     * @param monthStart 月份起始日期
     * @param monthEnd 月份结束日期
     * @return 员工薪资数据列表
     */
    List<StaffSalariesDTO> selectStaffSalaries(
            @Param("yearStart") LocalDate yearStart,
            @Param("yearEnd") LocalDate yearEnd,
            @Param("monthStart") LocalDate monthStart,
            @Param("monthEnd") LocalDate monthEnd
    );
}
