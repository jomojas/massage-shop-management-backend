package com.jiade.massageshopmanagement.dto;

import java.math.BigDecimal;

public class EmployeeInfo {
    private Long employeeId;
    private BigDecimal income;


    public Long getEmployeeId() {
        return employeeId;
    }
    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
    public BigDecimal getIncome() {
        return income;
    }
    public void setIncome(BigDecimal income) {
        this.income = income;
    }
}
