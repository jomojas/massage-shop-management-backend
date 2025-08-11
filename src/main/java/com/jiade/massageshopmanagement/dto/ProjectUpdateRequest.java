package com.jiade.massageshopmanagement.dto;

import java.math.BigDecimal;

public class ProjectUpdateRequest {
    private String name;
    private String category;
    private BigDecimal priceGuest;
    private BigDecimal priceMember;
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getPriceGuest() {
        return priceGuest;
    }

    public void setPriceGuest(BigDecimal priceGuest) {
        this.priceGuest = priceGuest;
    }

    public BigDecimal getPriceMember() {
        return priceMember;
    }

    public void setPriceMember(BigDecimal priceMember) {
        this.priceMember = priceMember;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
