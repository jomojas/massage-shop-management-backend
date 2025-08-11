package com.jiade.massageshopmanagement.dto;

import com.jiade.massageshopmanagement.dto.ProjectUpdateInfo;

import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConsumeRecordUpdateRequest {
    private String name;
    private String phone;
    private String description;
    private BigDecimal totalPrice;
    private LocalDateTime consumeTime;
    private List<ProjectUpdateInfo> projects;
    private String recordDetail;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    public LocalDateTime getConsumeTime() {
        return consumeTime;
    }
    public void setConsumeTime(LocalDateTime consumeTime) {
        this.consumeTime = consumeTime;
    }
    public List<ProjectUpdateInfo> getProjects() {
        return projects;
    }
    public void setProjects(List<ProjectUpdateInfo> projects) {
        this.projects = projects;
    }
    public String getRecordDetail() {
        return recordDetail;
    }
    public void setRecordDetail(String recordDetail) {
        this.recordDetail = recordDetail;
    }
}

