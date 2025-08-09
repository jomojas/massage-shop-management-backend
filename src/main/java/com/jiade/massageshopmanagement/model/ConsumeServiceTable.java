package com.jiade.massageshopmanagement.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConsumeServiceTable {
    private Long id;                 // 主键
    private Long consumeItemId;      // 项目明细ID（外键）
    private Long employeeId;         // 员工ID（外键）
    private BigDecimal earnings;     // 员工分成金额
    private Boolean isDeleted;       // 删除标志（0/1，建议Boolean类型）
    private LocalDateTime createTime;// 创建时间
    private LocalDateTime serviceTime;// 服务时间
    private BigDecimal commission;   // 技师提成

    // getter 和 setter 方法
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getConsumeItemId() { return consumeItemId; }
    public void setConsumeItemId(Long consumeItemId) { this.consumeItemId = consumeItemId; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public BigDecimal getEarnings() { return earnings; }
    public void setEarnings(BigDecimal earnings) { this.earnings = earnings; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getServiceTime() { return serviceTime; }
    public void setServiceTime(LocalDateTime serviceTime) { this.serviceTime = serviceTime; }

    public BigDecimal getCommission() { return commission; }
    public void setCommission(BigDecimal commission) { this.commission = commission; }
}
