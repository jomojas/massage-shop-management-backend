package com.jiade.massageshopmanagement.dto;

import com.jiade.massageshopmanagement.dto.ProjectInfo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ConsumeRecordRequest {
    private Long memberId;         // 会员ID（会员消费时有）
    private String customerDesc;      // 普通客户描述（普通用户消费时有）
    private List<ProjectInfo> projects;
    private LocalDateTime consumeTime;
    private BigDecimal totalPrice;
    private String recordDetail;

    public Long getMemberId() {
        return memberId;
    }
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }
    public String getCustomerDesc() {
        return customerDesc;
    }
    public void setCustomerDesc(String customerDesc) {
        this.customerDesc = customerDesc;
    }
    public List<ProjectInfo> getProjects() {
        return projects;
    }
    public void setProjects(List<ProjectInfo> projects) {
        this.projects = projects;
    }
    public LocalDateTime getConsumeTime() {
        return consumeTime;
    }
    public void setConsumeTime(LocalDateTime consumeTime) {
        this.consumeTime = consumeTime;
    }
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    public String getRecordDetail() {
        return recordDetail;
    }
    public void setRecordDetail(String recordDetail) {
        this.recordDetail = recordDetail;
    }
}
