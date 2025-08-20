package com.jiade.massageshopmanagement.dto.StatsDto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class StaffSalariesDTO {
    @JsonProperty("staff_id")
    private Long staffId;

    @JsonProperty("staff_name")
    private String staffName;

    @JsonProperty("total_salary")
    private BigDecimal totalSalary;

    @JsonProperty("year_salary")
    private BigDecimal yearSalary;

    @JsonProperty("month_salary")
    private BigDecimal monthSalary;

    @JsonProperty("week_salary")
    private BigDecimal weekSalary;

    public StaffSalariesDTO() {}

    public StaffSalariesDTO(Long staffId, String staffName, BigDecimal totalSalary,
                            BigDecimal yearSalary, BigDecimal monthSalary, BigDecimal weekSalary) {
        this.staffId = staffId;
        this.staffName = staffName;
        this.totalSalary = totalSalary;
        this.yearSalary = yearSalary;
        this.monthSalary = monthSalary;
        this.weekSalary = weekSalary;
    }

    // getter、setter
    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public BigDecimal getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(BigDecimal totalSalary) {
        this.totalSalary = totalSalary;
    }

    public BigDecimal getYearSalary() {
        return yearSalary;
    }

    public void setYearSalary(BigDecimal yearSalary) {
        this.yearSalary = yearSalary;
    }

    public BigDecimal getMonthSalary() {
        return monthSalary;
    }

    public void setMonthSalary(BigDecimal monthSalary) {
        this.monthSalary = monthSalary;
    }

    public BigDecimal getWeekSalary() {
        return weekSalary;
    }

    public void setWeekSalary(BigDecimal weekSalary) {
        this.weekSalary = weekSalary;
    }
}
