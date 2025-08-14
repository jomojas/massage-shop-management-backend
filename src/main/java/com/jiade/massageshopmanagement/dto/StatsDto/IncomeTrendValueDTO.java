package com.jiade.massageshopmanagement.dto.StatsDto;

import java.math.BigDecimal;

public class IncomeTrendValueDTO {
    private String label;
    private BigDecimal value;

    public IncomeTrendValueDTO() {}
    public IncomeTrendValueDTO(String label, BigDecimal value) {
        this.label = label;
        this.value = value;
    }
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
}
