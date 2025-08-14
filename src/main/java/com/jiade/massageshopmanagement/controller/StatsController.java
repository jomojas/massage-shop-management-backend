package com.jiade.massageshopmanagement.controller;

import com.jiade.massageshopmanagement.dto.ApiResponse;
import com.jiade.massageshopmanagement.dto.StatsDto.*;
import com.jiade.massageshopmanagement.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    @Autowired
    private StatsService statsService;

    // RedisTemplate用于缓存统计数据
    @GetMapping("/income-trend")
    public ApiResponse<IncomeTrendResponseDTO> getIncomeTrend(@RequestParam String period) {
        IncomeTrendResponseDTO response = statsService.getIncomeTrend(period);
        return ApiResponse.success(response);
    }

    @GetMapping("/net-income-trend")
    public ApiResponse<NetIncomeTrendResponseDTO> getNetIncomeTrend(@RequestParam String period) {
        NetIncomeTrendResponseDTO response = statsService.getNetIncomeTrend(period);
        return ApiResponse.success(response);
    }

    @GetMapping("/staff-income-trend")
    public ApiResponse<StaffIncomeTrendDataDTO> getStaffIncomeTrend(@RequestParam("period") String period) {
        StaffIncomeTrendDataDTO response = statsService.getStaffIncomeTrend(period);
        return ApiResponse.success(response);
    }

    @GetMapping("/consumption-ratio")
    public ApiResponse<List<ConsumptionRatioDTO>> getConsumptionRatio(@RequestParam("period") String period) {
        List<ConsumptionRatioDTO> response = statsService.getConsumptionRatio(period);
        return ApiResponse.success(response);
    }

    @GetMapping("/project-income-ratio")
    public ApiResponse<List<ProjectIncomeRatioDTO>> getProjectIncomeRatio(@RequestParam("period") String period) {
        List<ProjectIncomeRatioDTO> response = statsService.getProjectIncomeRatio(period);
        return ApiResponse.success(response);
    }

    @GetMapping("/summary")
    public ApiResponse<SummaryDTO> getSummary(@RequestParam("period") String period) {
        SummaryDTO response = statsService.getSummary(period);
        return ApiResponse.success(response);
    }

    @GetMapping("/member-consumption/{id}")
    public ApiResponse<List<MemberConsumptionDTO>> getMemberConsumption(@PathVariable("id") Long memberId) {
        List<MemberConsumptionDTO> response = statsService.getMemberConsumption(memberId);
        return ApiResponse.success(response);
    }

    @GetMapping("/staff-service/{id}")
    public ApiResponse<List<StaffServiceDTO>> getStaffService(@PathVariable("id") Long staffId) {
        List<StaffServiceDTO> response = statsService.getStaffService(staffId);
        return ApiResponse.success(response);
    }

    @GetMapping("/staff-salaries")
    public ApiResponse<List<StaffSalariesDTO>> getStaffSalaries() {
        List<StaffSalariesDTO> response = statsService.getStaffSalaries();
        return ApiResponse.success(response);
    }
}
