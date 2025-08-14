package com.jiade.massageshopmanagement.dto.StatsDto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MemberConsumptionDTO {
    private LocalDateTime consumeDate;
    private String project;
    private BigDecimal price;

    public MemberConsumptionDTO() {}

    public MemberConsumptionDTO(LocalDateTime consumeDate, String project, BigDecimal price) {
        this.consumeDate = consumeDate;
        this.project = project;
        this.price = price;
    }

    // getter、setter 保持不变
    public LocalDateTime getConsumeDate() {
        return consumeDate;
    }

    public void setConsumeDate(LocalDateTime consumeDate) {
        this.consumeDate = consumeDate;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
