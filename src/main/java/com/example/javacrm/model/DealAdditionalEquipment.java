package com.example.javacrm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "deal_additional_equipment")
public class DealAdditionalEquipment {
    @EmbeddedId
    private DealAdditionalEquipmentId id;

    @NotNull(message = "Количество не может быть пустым")
    @Min(value = 1, message = "Количество должно быть положительным")
    @Column(name = "count")
    private Integer count;

    @NotBlank(message = "Тип дополнительного оборудования не может быть пустым")
    @Column(name = "type_dop")
    private String type;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @MapsId("dealId")
    @JoinColumn(name = "deal_id")
    private Deal deal;

    @ManyToOne
    @MapsId("priceDop")
    @JoinColumn(name = "price_dop")
    private AdditionalEquipment equipment;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

@Embeddable
@Data
class DealAdditionalEquipmentId implements java.io.Serializable {
    private Long dealId;
    private BigDecimal priceDop;
} 

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "deal_additional_equipment")
public class DealAdditionalEquipment {
    @EmbeddedId
    private DealAdditionalEquipmentId id;

    @NotNull(message = "Количество не может быть пустым")
    @Min(value = 1, message = "Количество должно быть положительным")
    @Column(name = "count")
    private Integer count;

    @NotBlank(message = "Тип дополнительного оборудования не может быть пустым")
    @Column(name = "type_dop")
    private String type;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @MapsId("dealId")
    @JoinColumn(name = "deal_id")
    private Deal deal;

    @ManyToOne
    @MapsId("priceDop")
    @JoinColumn(name = "price_dop")
    private AdditionalEquipment equipment;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

@Embeddable
@Data
class DealAdditionalEquipmentId implements java.io.Serializable {
    private Long dealId;
    private BigDecimal priceDop;
} 