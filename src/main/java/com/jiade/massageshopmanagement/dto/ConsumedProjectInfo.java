package com.jiade.massageshopmanagement.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;

public class ConsumedProjectInfo {
    @JsonIgnore
    private Long consumeItemId;
    @JsonIgnore
    private Long consumeRecordId;
    private String projectName;
    private BigDecimal price;
    private List<String> employees;

    public ConsumedProjectInfo() {}

//    public ConsumedProjectInfo(int consumeItemId, int consumeRecordId, String projectName, BigDecimal price, List<String> employees) {
//        this.consumeItemId = consumeItemId;
//        this.consumeRecordId = consumeRecordId;
//        this.projectName = projectName;
//        this.price = price;
//        this.employees = employees;
//    }

    public Long getConsumeItemId() {
        return consumeItemId;
    }
    public void setConsumeItemId(Long consumeItemId) {
        this.consumeItemId = consumeItemId;
    }
    public Long getConsumeRecordId() {
        return consumeRecordId;
    }
    public void setConsumeRecordId(Long consumeRecordId) {
        this.consumeRecordId = consumeRecordId;
    }
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
    public List<String> getEmployees() {
        return employees;
    }
    public void setEmployees(List<String> employees) {
        this.employees = employees;
    }
}
