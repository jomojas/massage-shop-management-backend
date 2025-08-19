package com.jiade.massageshopmanagement.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiade.massageshopmanagement.config.CacheConfig;
import com.jiade.massageshopmanagement.dto.StatsDto.*;
import com.jiade.massageshopmanagement.mapper.StatsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
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
    @Autowired
    private ObjectMapper objectMapper;

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

    // 确保消费占比数据完整 （当这段时间只有会员消费，或只有普通客户消费，又或者没有消费记录，可能会出现返回的数据不全的情况）
    private List<ConsumptionRatioDTO> ensureCompleteRatioData(List<ConsumptionRatioDTO> ratioData) {
        Map<String, BigDecimal> dataMap = new HashMap<>();
        dataMap.put("会员", BigDecimal.ZERO);
        dataMap.put("普通顾客", BigDecimal.ZERO);

        // 填充查询到的数据
        for (ConsumptionRatioDTO item : ratioData) {
            dataMap.put(item.getType(), item.getAmount());
        }

        // 构建结果列表，保证顺序
        List<ConsumptionRatioDTO> result = new ArrayList<>();
        result.add(new ConsumptionRatioDTO("会员", dataMap.get("会员")));
        result.add(new ConsumptionRatioDTO("普通顾客", dataMap.get("普通顾客")));

        return result;
    }

    private List<ProjectIncomeRatioDTO> processTop6ProjectsWithOthers(List<ProjectIncomeRatioDTO> allProjectData) {
        List<ProjectIncomeRatioDTO> result = new ArrayList<>();

        if (allProjectData.isEmpty()) {
            return result;
        }

        // 如果项目总数 <= 6，直接返回所有项目
        if (allProjectData.size() <= 6) {
            return new ArrayList<>(allProjectData);
        }

        // 如果项目总数 > 6，取前6名，其余合并为"其他"
        // 1. 添加前6名项目
        for (int i = 0; i < 6; i++) {
            result.add(allProjectData.get(i));
        }

        // 2. 计算第7名及以后的项目总收益
        BigDecimal othersAmount = BigDecimal.ZERO;
        for (int i = 6; i < allProjectData.size(); i++) {
            othersAmount = othersAmount.add(allProjectData.get(i).getAmount());
        }

        // 3. 添加"其他"项目（只要有第7名及以后的项目）
        if (othersAmount.compareTo(BigDecimal.ZERO) > 0) {
            result.add(new ProjectIncomeRatioDTO("其他", othersAmount));
        }

        return result;
    }

    /**
     * 获取本周范围（周一到周日）
     * @return [本周一, 本周日]
     */
    private LocalDate[] getThisWeekRange() {
        LocalDate now = LocalDate.now();

        // 获取本周一（周一为一周的开始）
        LocalDate weekStart = now.with(DayOfWeek.MONDAY);

        // 获取本周日
        LocalDate weekEnd = now.with(DayOfWeek.SUNDAY);

        return new LocalDate[]{weekStart, weekEnd};
    }

    /**
     * 生成本周的缓存键标识
     * @return 周缓存键，格式：2025-W33
     */
    private String getWeekKey() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int weekOfYear = now.get(WeekFields.ISO.weekOfYear());
        return year + "-W" + String.format("%02d", weekOfYear);
    }


    public IncomeTrendResponseDTO getIncomeTrend(String period) {
        String redisKey = "stats:income-trend:" + period;
        Object cachedObj = redisTemplate.opsForValue().get(redisKey);
        if (cachedObj != null) {
            return objectMapper.convertValue(cachedObj, IncomeTrendResponseDTO.class);
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
        Object cachedObj = redisTemplate.opsForValue().get(redisKey);
        if (cachedObj != null) {
            return objectMapper.convertValue(cachedObj, NetIncomeTrendResponseDTO.class);
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
        Object cachedObj = redisTemplate.opsForValue().get(redisKey);
        if (cachedObj != null) {
            return objectMapper.convertValue(cachedObj, StaffIncomeTrendDataDTO.class);
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

    public List<ConsumptionRatioDTO> getConsumptionRatio(String period) {
        String redisKey = "stats:consumption-ratio:" + period;
        @SuppressWarnings("unchecked")
        Object cachedObj = redisTemplate.opsForValue().get(redisKey);
        if (cachedObj != null) {
            return objectMapper.convertValue(cachedObj, new TypeReference<List<ConsumptionRatioDTO>>() {});
        }

        // 获取时间范围
        LocalDate[] range = getStartAndEndByPeriod(period);
        LocalDate start = range[0], end = range[1];

        // period=all时，start可以为null，需特殊处理
        if ("all".equals(period) && start == null) {
            start = statsMapper.selectMinDate();
            if (start == null) {
                // 数据库没数据，返回空消费记录
                List<ConsumptionRatioDTO> emptyResult = new ArrayList<>();
                emptyResult.add(new ConsumptionRatioDTO("会员", BigDecimal.ZERO));
                emptyResult.add(new ConsumptionRatioDTO("普通顾客", BigDecimal.ZERO));
                return emptyResult;
            }
        }

        // 查询数据库获取消费占比数据
        List<ConsumptionRatioDTO> ratioData = statsMapper.selectConsumptionRatio(start, end);

        // 确保返回完整的占比数据（会员和普通顾客都有）
        List<ConsumptionRatioDTO> result = ensureCompleteRatioData(ratioData);

        // 缓存结果，过期时间30分钟
        redisTemplate.opsForValue().set(redisKey, result, cacheConfig.getDefaultExpireMinutes(), TimeUnit.MINUTES);

        return result;
    }

    public List<ProjectIncomeRatioDTO> getProjectIncomeRatio(String period) {
        String redisKey = "stats:project-income-ratio:" + period;
        Object cachedObj = redisTemplate.opsForValue().get(redisKey);
        if (cachedObj != null) {
            return objectMapper.convertValue(cachedObj, new TypeReference<List<ProjectIncomeRatioDTO>>() {});
        }

        // 获取时间范围
        LocalDate[] range = getStartAndEndByPeriod(period);
        LocalDate start = range[0], end = range[1];

        // period=all时，start可以为null，需特殊处理
        if ("all".equals(period) && start == null) {
            start = statsMapper.selectMinDate();
            if (start == null) {
                // 数据库没数据，返回空列表
                return new ArrayList<>();
            }
        }

        // 查询数据库获取项目收益占比数据
        List<ProjectIncomeRatioDTO> projectRatioData = statsMapper.selectProjectIncomeRatio(start, end);

        // 添加调试日志
//        System.out.println("原始查询结果数量: " + projectRatioData.size());
//        for (int i = 0; i < projectRatioData.size(); i++) {
//            ProjectIncomeRatioDTO item = projectRatioData.get(i);
//            System.out.println("第" + (i+1) + "名: " + item.getProject() + " - " + item.getAmount());
//        }

        // 处理前6名项目 + 其他项目合并
        List<ProjectIncomeRatioDTO> result = processTop6ProjectsWithOthers(projectRatioData);

        // 添加调试日志
//        System.out.println("处理后结果数量: " + result.size());
//        for (int i = 0; i < result.size(); i++) {
//            ProjectIncomeRatioDTO item = result.get(i);
//            System.out.println("最终第" + (i+1) + "项: " + item.getProject() + " - " + item.getAmount());
//        }

        // 缓存结果，过期时间30分钟
        redisTemplate.opsForValue().set(redisKey, projectRatioData, cacheConfig.getDefaultExpireMinutes(), TimeUnit.MINUTES);

        return result;
    }

    public SummaryDTO getSummary(String period) {
        String redisKey = "stats:summary:" + period;
        Object cachedObj = redisTemplate.opsForValue().get(redisKey);
        if (cachedObj != null) {
            // 推荐用objectMapper.convertValue来转为SummaryDTO
            return objectMapper.convertValue(cachedObj, SummaryDTO.class);
        }

        // 获取时间范围
        LocalDate[] range = getStartAndEndByPeriod(period);
        LocalDate start = range[0], end = range[1];

        // period=all时，start可以为null，需特殊处理
        if ("all".equals(period) && start == null) {
            start = statsMapper.selectMinDate();
            if (start == null) {
                // 数据库没数据，返回零值
                return new SummaryDTO(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            }
        }

        // 查询总收入（从消费记录）
        BigDecimal totalIncome = statsMapper.selectTotalIncome(start, end);
        if (totalIncome == null) {
            totalIncome = BigDecimal.ZERO;
        }

        // 查询总支出（从支出记录表，假设表名为expense_record）
        BigDecimal totalExpense = statsMapper.selectTotalExpense(start, end);
        if (totalExpense == null) {
            totalExpense = BigDecimal.ZERO;
        }

        // 计算净收入
        BigDecimal netIncome = totalIncome.subtract(totalExpense);

        SummaryDTO result = new SummaryDTO(totalIncome, totalExpense, netIncome);

        // 缓存结果，过期时间30分钟
        redisTemplate.opsForValue().set(redisKey, result, cacheConfig.getDefaultExpireMinutes(), TimeUnit.MINUTES);

        return result;
    }

    public List<MemberConsumptionDTO> getMemberConsumption(Long memberId) {
        String redisKey = "stats:member-consumption:" + memberId + ":" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        Object cachedObj = redisTemplate.opsForValue().get(redisKey);
        if (cachedObj != null) {
            return objectMapper.convertValue(cachedObj, new TypeReference<List<MemberConsumptionDTO>>() {});
        }

        // 获取本月的开始和结束日期
        LocalDate end = LocalDate.now();
        LocalDate start = end.withDayOfMonth(1);

        // 查询会员本月消费记录
        List<MemberConsumptionDTO> memberConsumption = statsMapper.selectMemberConsumption(memberId, start, end);

        // 缓存结果，过期时间30分钟
        redisTemplate.opsForValue().set(redisKey, memberConsumption, cacheConfig.getDefaultExpireMinutes(), TimeUnit.MINUTES);

        return memberConsumption;
    }

    public List<StaffServiceDTO> getStaffService(Long staffId) {
        String redisKey = "stats:staff-service:" + staffId + ":" + getWeekKey();
        Object cachedObj = redisTemplate.opsForValue().get(redisKey);
        if (cachedObj != null) {
            return objectMapper.convertValue(cachedObj, new TypeReference<List<StaffServiceDTO>>() {});
        }

        // 获取本月的开始和结束日期
        LocalDate end = LocalDate.now();
        LocalDate start = end.with(DayOfWeek.MONDAY);  // 本周一

        // 查询员工本周服务记录
        List<StaffServiceDTO> staffService = statsMapper.selectStaffService(staffId, start, end);


        // 缓存结果，过期时间30分钟
        redisTemplate.opsForValue().set(redisKey, staffService, cacheConfig.getDefaultExpireMinutes(), TimeUnit.MINUTES);

        return staffService;
    }


    public List<StaffSalariesDTO> getStaffSalaries() {
        String redisKey = "stats:staff-salaries:" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        Object cachedObj = redisTemplate.opsForValue().get(redisKey);
        if (cachedObj != null) {
            return objectMapper.convertValue(cachedObj, new TypeReference<List<StaffSalariesDTO>>() {});
        }

        // 获取当前年份和日期
        LocalDate now = LocalDate.now();
        int currentYear = now.getYear();

        // 获取本月的开始日期到当前日期
        LocalDate monthStart = now.withDayOfMonth(1);  // 本月1号
        LocalDate monthEnd = now;                      // 当前日期（今天）

        // 获取本年的开始日期到当前日期
        LocalDate yearStart = LocalDate.of(currentYear, 1, 1);  // 本年1月1号
        LocalDate yearEnd = now;                                // 当前日期（今天）

        // 查询所有员工的薪资统计
        List<StaffSalariesDTO> staffSalaries = statsMapper.selectStaffSalaries(yearStart, yearEnd, monthStart, monthEnd);

        // 缓存结果，过期时间30分钟
        redisTemplate.opsForValue().set(redisKey, staffSalaries, cacheConfig.getDefaultExpireMinutes(), TimeUnit.MINUTES);

        return staffSalaries;
    }
}
