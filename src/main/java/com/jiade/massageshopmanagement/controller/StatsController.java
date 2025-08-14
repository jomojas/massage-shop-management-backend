package com.jiade.massageshopmanagement.controller;

import com.jiade.massageshopmanagement.dto.ApiResponse;
import com.jiade.massageshopmanagement.dto.StatsDto.IncomeTrendResponseDTO;
import com.jiade.massageshopmanagement.dto.StatsDto.NetIncomeTrendResponseDTO;
import com.jiade.massageshopmanagement.dto.StatsDto.StaffIncomeTrendDataDTO;
import com.jiade.massageshopmanagement.service.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
