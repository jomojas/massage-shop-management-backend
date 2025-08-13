package com.jiade.massageshopmanagement.enums;

public enum OperationType {
    CREATE("新增"),
    UPDATE("修改"),
    DELETE("删除"),
    RESTORE("恢复");

    private final String description;

    OperationType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
