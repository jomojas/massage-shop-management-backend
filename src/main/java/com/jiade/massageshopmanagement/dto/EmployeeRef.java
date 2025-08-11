package com.jiade.massageshopmanagement.dto;

import java.math.BigDecimal;

public class EmployeeRef {
    private Long id;
    private String name;
    private BigDecimal income; // 员工在该项目的收益

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public  BigDecimal getIncome() {
        return income;
    }
    public void setIncome(BigDecimal income) {
        this.income = income;
    }
}
