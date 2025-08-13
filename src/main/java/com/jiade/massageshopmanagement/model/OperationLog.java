package com.jiade.massageshopmanagement.model;

import com.jiade.massageshopmanagement.enums.OperationModule;
import com.jiade.massageshopmanagement.enums.OperationType;

import java.time.LocalDateTime;

public class OperationLog {
    private Long id;
    private OperationType operation;   // 推荐用枚举类型
    private OperationModule module;    // 推荐用枚举类型
    private String detail;
    private LocalDateTime operationTime;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public OperationType getOperation() {
        return operation;
    }
    public void setOperation(OperationType operation) {
        this.operation = operation;
    }
    public OperationModule getModule() {
        return module;
    }
    public void setModule(OperationModule module) {
        this.module = module;
    }
    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
    public LocalDateTime getOperationTime() {
        return operationTime;
    }
    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }
}
