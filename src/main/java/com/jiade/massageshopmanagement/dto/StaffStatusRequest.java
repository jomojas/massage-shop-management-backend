package com.jiade.massageshopmanagement.dto;

import java.time.LocalDate;

public class StaffStatusRequest {
    private LocalDate date;   // 格式如 "2025-08-05"
    private String status; // 如 "到岗"
    private String remark; // 可选备注

    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public  String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
