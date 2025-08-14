package com.jiade.massageshopmanagement.dto.StatsDto;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class StaffIncomeTrendDataDTO {
    private List<List<Object>> source;

    // 内部类：用于接收SQL查询结果
    public static class StaffIncome {
        private String staffName;
        private LocalDate date;
        private BigDecimal income;

        // 构造方法、getter、setter
        public StaffIncome() {}

        public StaffIncome(String staffName, LocalDate date, BigDecimal income) {
            this.staffName = staffName;
            this.date = date;
            this.income = income;
        }

        // getter、setter
        public String getStaffName() { return staffName; }
        public void setStaffName(String staffName) { this.staffName = staffName; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public BigDecimal getIncome() { return income; }
        public void setIncome(BigDecimal income) { this.income = income; }
    }

    // 构造方法
    public StaffIncomeTrendDataDTO() {}

    public StaffIncomeTrendDataDTO(List<List<Object>> source) {
        this.source = source;
    }

    // 从查询结果构造方法
    public StaffIncomeTrendDataDTO(String period, List<StaffIncome> staffIncomes, LocalDate actualStart, LocalDate actualEnd) {
        this.source = buildSourceFromData(period, staffIncomes, actualStart, actualEnd);
    }

    private List<List<Object>> buildSourceFromData(String period, List<StaffIncome> staffIncomes, LocalDate actualStart, LocalDate actualEnd) {
        if (staffIncomes.isEmpty()) {
            return Collections.emptyList();
        }

        // 1. 提取所有员工名
        Set<String> staffNames = new LinkedHashSet<>();
        for (StaffIncome item : staffIncomes) {
            staffNames.add(item.getStaffName());
        }

        // 2. 构建员工收益映射
        Map<String, Map<LocalDate, BigDecimal>> staffIncomeMap = new HashMap<>();
        for (String staffName : staffNames) {
            staffIncomeMap.put(staffName, new HashMap<>());
        }

        for (StaffIncome item : staffIncomes) {
            staffIncomeMap.get(item.getStaffName()).put(item.getDate(), item.getIncome());
        }

        // 3. 根据实际的 start 和 end 生成完整的日期范围
        Set<LocalDate> allDates = generateDateRangeWithActualDates(period, actualStart, actualEnd);

        // 4. 补全所有员工在所有日期的数据为0
        for (String staffName : staffNames) {
            Map<LocalDate, BigDecimal> incomeMap = staffIncomeMap.get(staffName);
            for (LocalDate date : allDates) {
                incomeMap.putIfAbsent(date, BigDecimal.ZERO);
            }
        }

        // 5. 构建 List<List<Object>> 结构
        List<List<Object>> result = new ArrayList<>();

        // 添加表头
        List<Object> header = new ArrayList<>();
        header.add("staff_name");
        for (LocalDate date : allDates) {
            header.add(formatDateByPeriod(date, period));
        }
        result.add(header);

        // 添加每个员工的数据行
        for (String staffName : staffNames) {
            List<Object> row = new ArrayList<>();
            row.add(staffName);

            for (LocalDate date : allDates) {
                BigDecimal income = staffIncomeMap.get(staffName).get(date);
                row.add(income.intValue());
            }
            result.add(row);
        }

        return result;
    }

    // 生成指定period的日期范围
    private Set<LocalDate> generateDateRangeWithActualDates(String period, LocalDate start, LocalDate end) {
        Set<LocalDate> dates = new TreeSet<>();

        switch (period) {
            case "week":
            case "month":
                // 按天生成
                LocalDate current = start;
                while (!current.isAfter(end)) {
                    dates.add(current);
                    current = current.plusDays(1);
                }
                break;

            case "year":
            case "all":
                // 按月生成（每月的第一天）
                LocalDate currentMonth = start.withDayOfMonth(1);
                LocalDate endMonth = end.withDayOfMonth(1);
                while (!currentMonth.isAfter(endMonth)) {
                    dates.add(currentMonth);
                    currentMonth = currentMonth.plusMonths(1);
                }
                break;
        }

        return dates;
    }

    // 根据period格式化日期显示
    private String formatDateByPeriod(LocalDate date, String period) {
        return switch (period) {
            case "week", "month" -> date.toString(); // 2025-08-14
            case "year", "all" -> date.toString().substring(0, 7); // 2025-08
            default -> date.toString();
        };
    }

    // getter、setter
    public List<List<Object>> getSource() {
        return source;
    }

    public void setSource(List<List<Object>> source) {
        this.source = source;
    }
}
