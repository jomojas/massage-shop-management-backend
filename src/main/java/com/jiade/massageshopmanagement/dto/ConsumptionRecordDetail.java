package com.jiade.massageshopmanagement.dto;

import com.jiade.massageshopmanagement.dto.ConsumedProjectInfo;
//import com.baomidou.mybatisplus.annotation.TableField;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ConsumptionRecordDetail {
    private Long recordId;
    private String name;
    private String phone;
    private String description;
    private BigDecimal totalPrice;
    private LocalDateTime consumeTime;
//    @TableField(exist = false)
    private List<ConsumedProjectInfo> projects;
    private String recordDetail;

    public ConsumptionRecordDetail() {}
//    public ConsumptionRecordDetail(int recordId, String name, String phone, String description, BigDecimal totalPrice,
//                                   LocalDateTime consumeTime, List<ConsumedProjectInfo> projects, String recordDetail) {
//        this.recordId = recordId;
//        this.name = name;
//        this.phone = phone;
//        this.description = description;
//        this.totalPrice = totalPrice;
//        this.consumeTime = consumeTime;
//        this.projects = projects;
//        this.recordDetail = recordDetail;
//    }

    public Long getRecordId() {
        return recordId;
    }
    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }
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
    public List<ConsumedProjectInfo> getProjects() {
        return projects;
    }
    public void setProjects(List<ConsumedProjectInfo> projects) {
        this.projects = projects;
    }
    public String getRecordDetail() {
        return recordDetail;
    }
    public void setRecordDetail(String recordDetail) {
        this.recordDetail = recordDetail;
    }
}
