package com.jiade.massageshopmanagement.dto.StatsDto;

import java.math.BigDecimal;

public class ProjectIncomeRatioDTO {
    private String project;
    private BigDecimal amount;

    public ProjectIncomeRatioDTO() {}

    public ProjectIncomeRatioDTO(String project, BigDecimal amount) {
        this.project = project;
        this.amount = amount;
    }

    // getter、setter
    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
