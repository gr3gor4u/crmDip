package com.example.javacrm.model;

import java.time.LocalDateTime;

public class DealEquipment {
    private Long dealId;
    private Long equipmentId;
    private Integer quantity;
    private Double price;
    private LocalDateTime createdAt;
    private AdditionalEquipment equipment;

    public DealEquipment() {
    }

    public DealEquipment(Long dealId, Long equipmentId, Integer quantity, Double price) {
        this.dealId = dealId;
        this.equipmentId = equipmentId;
        this.quantity = quantity;
        this.price = price;
        this.createdAt = LocalDateTime.now();
    }

    public Long getDealId() {
        return dealId;
    }

    public void setDealId(Long dealId) {
        this.dealId = dealId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public AdditionalEquipment getEquipment() {
        return equipment;
    }

    public void setEquipment(AdditionalEquipment equipment) {
        this.equipment = equipment;
    }
} 