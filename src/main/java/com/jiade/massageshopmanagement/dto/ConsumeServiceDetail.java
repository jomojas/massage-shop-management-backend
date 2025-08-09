package com.jiade.massageshopmanagement.dto;

import java.math.BigDecimal;

public class ConsumeServiceDetail {
    private Long consumeServiceId; // 服务项ID
    private Long consumeItemId;    // 项目项ID
    private Long employeeId;       // 员工ID
    private String employeeName;      // 员工姓名
    private BigDecimal earnings;          // 服务分成/收入

    public ConsumeServiceDetail() {}

//    public ConsumeServiceDetail(int consumeServiceId, int consumeItemId, int employeeId, String employeeName, BigDecimal earnings) {
//        this.consumeServiceId = consumeServiceId;
//        this.consumeItemId = consumeItemId;
//        this.employeeId = employeeId;
//        this.employeeName = employeeName;
//        this.earnings = earnings;
//    }

    public Long getConsumeServiceId() {
        return consumeServiceId;
    }
    public void setConsumeServiceId(Long consumeServiceId) {
        this.consumeServiceId = consumeServiceId;
    }

    public Long getConsumeItemId() {
        return consumeItemId;
    }

    public void setConsumeItemId(Long consumeItemId) {
        this.consumeItemId = consumeItemId;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public BigDecimal getEarnings() {
        return earnings;
    }

    public void setEarnings(BigDecimal earnings) {
        this.earnings = earnings;
    }
}
