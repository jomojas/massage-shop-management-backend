package com.jiade.massageshopmanagement.model;

import java.math.BigDecimal;

public class ConsumeItem {
    private Long id;
    private Long consumeRecordId;  // 消费记录ID（外键）
    private Long projectId;        // 项目ID（外键）
    private BigDecimal price;         // 项目价格
    private String remark;            // 备注
    private Boolean isDeleted;        // 删除标志（0/1）

    // getter 和 setter 方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getConsumeRecordId() { return consumeRecordId; }
    public void setConsumeRecordId(Long consumeRecordId) { this.consumeRecordId = consumeRecordId; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
}
