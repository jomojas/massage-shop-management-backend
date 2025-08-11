package com.jiade.massageshopmanagement.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Project {
    private Long id;
    private String name;
    private String category;
    private BigDecimal priceGuest;
    private BigDecimal priceMember;
    private String description;
    private Integer isDeleted;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;

    public Project() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getIsDeleted() {
        return isDeleted;
    }
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }
    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}
