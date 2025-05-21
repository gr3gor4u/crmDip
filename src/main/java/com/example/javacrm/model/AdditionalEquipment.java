package com.example.javacrm.model;

import java.time.LocalDateTime;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AdditionalEquipment {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private Boolean available;
    private LocalDateTime createdAt;

    public AdditionalEquipment() {
    }

    public AdditionalEquipment(Long id, String name, BigDecimal price, Integer quantity, Boolean available) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.available = available;
        this.createdAt = LocalDateTime.now();
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 