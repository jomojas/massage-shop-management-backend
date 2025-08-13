package com.jiade.massageshopmanagement.dto;

import java.util.List;

public class LogCategoriesResponse {
    private List<EnumItem> operationTypes;
    private List<EnumItem> moduleTypes;

    public LogCategoriesResponse() {}

    public LogCategoriesResponse(List<EnumItem> operationTypes, List<EnumItem> moduleTypes) {
        this.operationTypes = operationTypes;
        this.moduleTypes = moduleTypes;
    }

    public List<EnumItem> getOperationTypes() {
        return operationTypes;
    }

    public void setOperationTypes(List<EnumItem> operationTypes) {
        this.operationTypes = operationTypes;
    }

    public List<EnumItem> getModuleTypes() {
        return moduleTypes;
    }

    public void setModuleTypes(List<EnumItem> moduleTypes) {
        this.moduleTypes = moduleTypes;
    }
}
