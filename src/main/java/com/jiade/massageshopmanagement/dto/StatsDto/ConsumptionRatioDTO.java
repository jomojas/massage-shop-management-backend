package com.jiade.massageshopmanagement.dto.StatsDto;

import java.math.BigDecimal;

public class ConsumptionRatioDTO {
    private String type;
    private BigDecimal amount;

    public ConsumptionRatioDTO() {}

    public ConsumptionRatioDTO(String type, BigDecimal amount) {
        this.type = type;
        this.amount = amount;
    }

    // getter、setter
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
