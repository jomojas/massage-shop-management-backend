package com.jiade.massageshopmanagement.dto.StatsDto;

import java.math.BigDecimal;

public class SummaryDTO {
    private BigDecimal totalIncome;    // 总收入
    private BigDecimal totalExpense;   // 总支出
    private BigDecimal netIncome;      // 净收入

    public SummaryDTO() {}

    public SummaryDTO(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal netIncome) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.netIncome = netIncome;
    }

    // getter、setter
    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(BigDecimal totalExpense) {
        this.totalExpense = totalExpense;
    }

    public BigDecimal getNetIncome() {
        return netIncome;
    }

    public void setNetIncome(BigDecimal netIncome) {
        this.netIncome = netIncome;
    }
}
