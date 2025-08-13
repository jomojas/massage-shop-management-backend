package com.jiade.massageshopmanagement.enums;

public enum OperationModule {
    MEMBER("会员管理"),
    CONSUMPTION("消费管理"),
    PROJECT("项目管理"),
    STAFF("员工管理"),
    STAFF_STATUS("员工状态管理"),
    EXPENSE("商店支出管理");

    private final String description;

    OperationModule(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
