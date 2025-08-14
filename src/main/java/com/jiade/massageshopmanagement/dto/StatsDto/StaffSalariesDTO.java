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

    public StaffSalariesDTO() {}

    public StaffSalariesDTO(Long staffId, String staffName, BigDecimal totalSalary,
                            BigDecimal yearSalary, BigDecimal monthSalary) {
        this.staffId = staffId;
        this.staffName = staffName;
        this.totalSalary = totalSalary;
        this.yearSalary = yearSalary;
        this.monthSalary = monthSalary;
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
}
