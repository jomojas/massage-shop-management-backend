package com.jiade.massageshopmanagement.dto;

import com.jiade.massageshopmanagement.dto.EmployeeInfo;

import java.math.BigDecimal;
import java.util.List;

public class ProjectInfo {
    private String projectName;
    private BigDecimal price;
    private List<EmployeeInfo> employees;

    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public List<EmployeeInfo> getEmployees() {
        return employees;
    }
    public void setEmployees(List<EmployeeInfo> employees) {
        this.employees = employees;
    }
}
