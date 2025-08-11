package com.jiade.massageshopmanagement.dto;

import com.jiade.massageshopmanagement.dto.EmployeeRef;
import java.math.BigDecimal;
import java.util.List;

public class ProjectUpdateInfo {
    private String projectName;
    private BigDecimal price;
    private List<EmployeeRef> employees; // 员工姓名列表

    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public List<EmployeeRef> getEmployees() {
        return employees;
    }
    public void setEmployees(List<EmployeeRef> employees) {
        this.employees = employees;
    }
}
