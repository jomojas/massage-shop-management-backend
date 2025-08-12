package com.jiade.massageshopmanagement.dto;

import java.math.BigDecimal;

public class StaffUpdateRequest {
    private String name;
    private String phone;
    private BigDecimal commission;

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
    public BigDecimal getCommission() {
        return commission;
    }
    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }
}
