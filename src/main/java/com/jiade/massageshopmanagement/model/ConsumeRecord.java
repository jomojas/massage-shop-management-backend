package com.jiade.massageshopmanagement.model;


import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConsumeRecord {
    private Long id;
    private String userType;      // "MEMBER" 或 "GUEST"
    private Long userId;       // 会员 ID，普通用户为 null
    private String description;   // 会员为序号，普通用户为姓名+电话等描述
    private BigDecimal totalPrice;
    private LocalDateTime createTime;
    private LocalDateTime consumeTime;
    private Boolean isDeleted;    // 0/1
    private LocalDateTime updatedTime;
    private String recordDetail;  // 详细消费内容

    // getter 和 setter 方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getConsumeTime() { return consumeTime; }
    public void setConsumeTime(LocalDateTime consumeTime) { this.consumeTime = consumeTime; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }

    public String getRecordDetail() { return recordDetail; }
    public void setRecordDetail(String recordDetail) { this.recordDetail = recordDetail; }
}
