package com.jiade.massageshopmanagement.service;

import com.jiade.massageshopmanagement.config.CacheConfig;
import com.jiade.massageshopmanagement.dto.StatsDto.IncomeTrendResponseDTO;
import com.jiade.massageshopmanagement.dto.StatsDto.IncomeTrendValueDTO;
import com.jiade.massageshopmanagement.dto.StatsDto.NetIncomeTrendResponseDTO;
import com.jiade.massageshopmanagement.dto.StatsDto.StaffIncomeTrendDataDTO;
import com.jiade.massageshopmanagement.mapper.StatsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class StatsService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private StatsMapper statsMapper;
    @Autowired
    private CacheConfig cacheConfig;

    // 根据period确定dimension
    private String getDimensionByPeriod(String period) {
        return switch (period) {
            case "week", "month" -> "day";
            case "year", "all" -> "month";
            default -> "day";
        };
    }

    public static LocalDate[] getStartAndEndByPeriod(String period) {
        LocalDate today = LocalDate.now();
        LocalDate start;
        LocalDate end = today;
        start = switch (period) {
            case "week" -> today.minusDays(today.getDayOfWeek().getValue() - 1); // 本周一
            case "month" -> today.withDayOfMonth(1); // 本月1号
            case "year" -> today.withDayOfYear(1); // 本年1月1日
            case "all" -> null; // 特殊处理，后面查最早记录时间或SQL不加start条件
            default -> today;
        };
        return new LocalDate[]{start, end};
    }

    // 补全空值日期为0
    public static List<IncomeTrendValueDTO> fillDateGaps(List<IncomeTrendValueDTO> values,
                                                         LocalDate start,
                                                         LocalDate end,
                                                         String dimension) {
        // 1. 转为map方便查找
        Map<String, BigDecimal> valueMap = values.stream()
                .collect(Collectors.toMap(
                        IncomeTrendValueDTO::getLabel,
                        IncomeTrendValueDTO::getValue,
                        (v1, v2) -> v1
                ));

        List<IncomeTrendValueDTO> fullList = new ArrayList<>();
        DateTimeFormatter formatter = "day".equals(dimension)
                ? DateTimeFormatter.ofPattern("yyyy-MM-dd")
                : DateTimeFormatter.ofPattern("yyyy-MM");

        if ("day".equals(dimension)) {
            LocalDate date = start;
            boolean needFill = false;
            while (!date.isAfter(end)) {
                String label = date.format(formatter);
                BigDecimal v = valueMap.getOrDefault(label, null);
                if (v == null) {
                    needFill = true;
                    v = BigDecimal.ZERO;
                }
                fullList.add(new IncomeTrendValueDTO(label, v));
                date = date.plusDays(1);
            }
            return needFill ? fullList : values;
        }
        if ("month".equals(dimension)) {
            LocalDate date = LocalDate.of(start.getYear(), start.getMonth(), 1);
            LocalDate endMonth = LocalDate.of(end.getYear(), end.getMonth(), 1);
            boolean needFill = false;
            while (!date.isAfter(endMonth)) {
                String label = date.format(formatter);
                BigDecimal v = valueMap.getOrDefault(label, null);
                if (v == null) {
                    needFill = true;
                    v = BigDecimal.ZERO;
                }
                fullList.add(new IncomeTrendValueDTO(label, v));
                date = date.plusMonths(1);
            }
            return needFill ? fullList : values;
        }
        // 其他维度直接返回原数据
        return values;
    }


    public IncomeTrendResponseDTO getIncomeTrend(String period) {
        String redisKey = "stats:income-trend:" + period;
        IncomeTrendResponseDTO cached = (IncomeTrendResponseDTO) redisTemplate.opsForValue().get(redisKey);
        if (cached != null) {
            return cached;
        }

        // 自动确定 dimension
        String dimension = getDimensionByPeriod(period);

        // 自动确定时间范围
        LocalDate[] range = getStartAndEndByPeriod(period);
        LocalDate start = range[0];
        LocalDate end = range[1];

        // period=all时，start可以为null，需特殊处理
        if ("all".equals(period) && start == null) {
            start = statsMapper.selectMinDate();
            if (start == null) {
                // 数据库没数据，直接返回空
                return new IncomeTrendResponseDTO(period, dimension, Collections.emptyList());
            }
        }

        // 查询数据库
        List<IncomeTrendValueDTO> values = statsMapper.selectIncomeTrend(start, end, dimension);
        List<IncomeTrendValueDTO> filledValues = fillDateGaps(values, start, end, dimension);

        IncomeTrendResponseDTO result = new IncomeTrendResponseDTO(period, dimension, filledValues);

        // 写入缓存，过期时间30分钟
        redisTemplate.opsForValue().set(redisKey, result, cacheConfig.getDefaultExpireMinutes(), TimeUnit.MINUTES);

        return result;
    }

    public NetIncomeTrendResponseDTO getNetIncomeTrend(String period) {
        String redisKey = "stats:net-income-trend:" + period;
        NetIncomeTrendResponseDTO cached = (NetIncomeTrendResponseDTO) redisTemplate.opsForValue().get(redisKey);
        if (cached != null) {
            return cached;
        }
        String dimension = getDimensionByPeriod(period);
        LocalDate[] range = getStartAndEndByPeriod(period);
        LocalDate start = range[0], end = range[1];

        if ("all".equals(period) && start == null) {
            start = statsMapper.selectMinDate();
            if (start == null) {
                // 没有收入数据，直接返回空
                return new NetIncomeTrendResponseDTO(period, dimension, Collections.emptyList());
            }
        }

//        System.out.println("start: " + start);
//        System.out.println("end: " + end);
        // 1. 查询收入和支出
        List<IncomeTrendValueDTO> incomeList = statsMapper.selectIncomeTrend(start, end, dimension);
        List<IncomeTrendValueDTO> expenseList = statsMapper.selectExpenseTrend(start, end, dimension);

        // 2. 补全所有日期的收入和支出
        List<IncomeTrendValueDTO> filledIncome = fillDateGaps(incomeList, start, end, dimension);
        List<IncomeTrendValueDTO> filledExpense = fillDateGaps(expenseList, start, end, dimension);

        // 3. 计算净收入并汇总
        List<IncomeTrendValueDTO> netIncomeList = new ArrayList<>();

        // 假设 filledIncome 的 label 顺序与所有日期完全一致
        for (int i = 0; i < filledIncome.size(); i++) {
            String label = filledIncome.get(i).getLabel();
            BigDecimal income = filledIncome.get(i).getValue();
            BigDecimal expense = filledExpense.get(i).getValue(); // 索引i与income一一对应
            BigDecimal netIncome = income.subtract(expense);
            netIncomeList.add(new IncomeTrendValueDTO(label, netIncome));
        }

        // 4. 排序（可选，按时间顺序）
        netIncomeList.sort(Comparator.comparing(IncomeTrendValueDTO::getLabel));

        NetIncomeTrendResponseDTO result = new NetIncomeTrendResponseDTO(period, dimension, netIncomeList);

        // 写入缓存，过期时间30分钟
        redisTemplate.opsForValue().set(redisKey, result, cacheConfig.getDefaultExpireMinutes(), TimeUnit.MINUTES);

        return result;
    }

    public StaffIncomeTrendDataDTO getStaffIncomeTrend(String period) {
        String redisKey = "stats:staff-income-trend:" + period;
        StaffIncomeTrendDataDTO cached = (StaffIncomeTrendDataDTO) redisTemplate.opsForValue().get(redisKey);
        if (cached != null) {
            return cached;
        }

        // 自动确定时间范围
        LocalDate[] range = getStartAndEndByPeriod(period);
        LocalDate start = range[0], end = range[1];

        // period=all时，start可以为null，需特殊处理
        if ("all".equals(period) && start == null) {
            start = statsMapper.selectMinDate();
            if (start == null) {
                // 数据库没数据，直接返回空
                return new StaffIncomeTrendDataDTO(Collections.emptyList());
            }
        }

        // 一个SQL查询所有员工收益数据
        List<StaffIncomeTrendDataDTO.StaffIncome> staffIncomes = statsMapper.selectStaffIncomeTrend(start, end, period);

        // 构造函数会自动处理数据补全和格式化
        StaffIncomeTrendDataDTO result = new StaffIncomeTrendDataDTO(period, staffIncomes, start, end);

        redisTemplate.opsForValue().set(redisKey, result, cacheConfig.getDefaultExpireMinutes(), TimeUnit.MINUTES);

        return result;
    }
}
