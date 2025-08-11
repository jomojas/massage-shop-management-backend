package com.jiade.massageshopmanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class StaffServiceRecordDTO {
    private Long recordId;
    private Long staffId;
    private String staffName;
    private String projectName;
    private BigDecimal earnings;
    private String customerName;
    private String customerDesc;
    private LocalDateTime serviceTime;
    private BigDecimal commission;
    private String recordDetail;

    public Long getRecordId() {
        return recordId;
    }
    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }
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
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public BigDecimal getEarnings() {
        return earnings;
    }
    public void setEarnings(BigDecimal earnings) {
        this.earnings = earnings;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getCustomerDesc() {
        return customerDesc;
    }
    public void setCustomerDesc(String customerDesc) {
        this.customerDesc = customerDesc;
    }
    public LocalDateTime getServiceTime() {
        return serviceTime;
    }
    public void setServiceTime(LocalDateTime serviceTime) {
        this.serviceTime = serviceTime;
    }
    public BigDecimal getCommission() {
        return commission;
    }
    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }
    public String getRecordDetail() {
        return recordDetail;
    }
    public void setRecordDetail(String recordDetail) {
        this.recordDetail = recordDetail;
    }
}
