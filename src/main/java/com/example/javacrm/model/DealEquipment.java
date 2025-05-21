package com.example.javacrm.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DealEquipment {
    private Long id;
    private Long dealId;
    private Long equipmentId;
    private Integer quantity;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private AdditionalEquipment equipment;
    private boolean noEquipment;

    public DealEquipment() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDealId() { return dealId; }
    public void setDealId(Long dealId) { this.dealId = dealId; }
    public Long getEquipmentId() { return equipmentId; }
    public void setEquipmentId(Long equipmentId) { this.equipmentId = equipmentId; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public AdditionalEquipment getEquipment() { return equipment; }
    public void setEquipment(AdditionalEquipment equipment) { this.equipment = equipment; }
    public boolean isNoEquipment() { return noEquipment; }
    public void setNoEquipment(boolean noEquipment) { this.noEquipment = noEquipment; }
} 