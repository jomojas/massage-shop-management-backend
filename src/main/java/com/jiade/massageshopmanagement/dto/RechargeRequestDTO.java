package com.jiade.massageshopmanagement.dto;

import java.math.BigDecimal;

public class RechargeRequestDTO {
    private BigDecimal amount; // Using String to avoid floating point precision issues
    private String remark;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
