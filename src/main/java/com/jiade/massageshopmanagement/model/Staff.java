package com.jiade.massageshopmanagement.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Staff {
    private Long id;
    private String name;
    private String phone;
    private BigDecimal commission;
    private Boolean isDeleted; // true: not deleted, false: deleted
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public BigDecimal getCommission() {
        return commission;
    }
    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }
    public boolean getIsDeleted() {
        return isDeleted != null ? isDeleted : Boolean.FALSE;
    }
    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
