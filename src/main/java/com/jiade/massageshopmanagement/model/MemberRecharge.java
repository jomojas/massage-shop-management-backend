package com.jiade.massageshopmanagement.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MemberRecharge {
    private Long id;
    private Long memberId;
    private BigDecimal amount;
    private String remark;
    private LocalDateTime rechargeTime;

    public MemberRecharge() {}

    public MemberRecharge(Long id, Long memberId, BigDecimal amount, String remark, LocalDateTime rechargeTime) {
        this.id = id;
        this.memberId = memberId;
        this.amount = amount;
        this.remark = remark;
        this.rechargeTime = rechargeTime;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public LocalDateTime getRechargeTime() { return rechargeTime; }
    public void setRechargeTime(LocalDateTime rechargeTime) { this.rechargeTime = rechargeTime; }
}
