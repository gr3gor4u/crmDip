package com.example.javacrm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "additional_equipment")
public class AdditionalEquipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equip")
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    @Column(name = "name")
    private String name;

    @NotNull(message = "Цена не может быть пустой")
    @DecimalMin(value = "0.0", message = "Цена должна быть положительной")
    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "tonirovka")
    private String tinting;

    @Column(name = "diski")
    private String wheels;

    @Column(name = "second_komplekt")
    private String secondSet;

    @Column(name = "kovriki")
    private String mats;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "additional_equipment")
public class AdditionalEquipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_equip")
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    @Column(name = "name")
    private String name;

    @NotNull(message = "Цена не может быть пустой")
    @DecimalMin(value = "0.0", message = "Цена должна быть положительной")
    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "tonirovka")
    private String tinting;

    @Column(name = "diski")
    private String wheels;

    @Column(name = "second_komplekt")
    private String secondSet;

    @Column(name = "kovriki")
    private String mats;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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