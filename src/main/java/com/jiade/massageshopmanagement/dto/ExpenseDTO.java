package com.jiade.massageshopmanagement.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseDTO {
    private String category;
    private BigDecimal amount;
    private LocalDate spendDate;
    private String description;

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public LocalDate getSpendDate() {
        return spendDate;
    }
    public void setSpendDate(LocalDate spendDate) {
        this.spendDate = spendDate;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
