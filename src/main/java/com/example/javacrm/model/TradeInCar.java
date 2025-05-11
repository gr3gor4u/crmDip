package com.example.javacrm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "trade_in_car")
public class TradeInCar {
    @Id
    @Column(name = "vin_number_trade")
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "Неверный формат VIN-номера")
    private String vinNumber;

    @NotBlank(message = "Модель не может быть пустой")
    @Column(name = "model_car")
    private String model;

    @NotNull(message = "Цена не может быть пустой")
    @DecimalMin(value = "0.0", message = "Цена должна быть положительной")
    @Column(name = "price_car_trade")
    private BigDecimal price;

    @NotBlank(message = "Марка не может быть пустой")
    @Column(name = "marka")
    private String brand;

    @NotBlank(message = "Тип кузова не может быть пустым")
    @Column(name = "type_kuzov")
    private String bodyType;

    @Column(name = "description")
    private String description;

    @Column(name = "mileage")
    @Min(value = 0, message = "Пробег должен быть положительным")
    private Integer mileage;

    @Column(name = "year")
    @Min(value = 1900, message = "Год должен быть не раньше 1900")
    @Max(value = 2100, message = "Год должен быть не позже 2100")
    private Integer year;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "id_chelovek")
    private Customer customer;

    @Column(name = "condition")
    private String condition;

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
@Table(name = "trade_in_car")
public class TradeInCar {
    @Id
    @Column(name = "vin_number_trade")
    @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "Неверный формат VIN-номера")
    private String vinNumber;

    @NotBlank(message = "Модель не может быть пустой")
    @Column(name = "model_car")
    private String model;

    @NotNull(message = "Цена не может быть пустой")
    @DecimalMin(value = "0.0", message = "Цена должна быть положительной")
    @Column(name = "price_car_trade")
    private BigDecimal price;

    @NotBlank(message = "Марка не может быть пустой")
    @Column(name = "marka")
    private String brand;

    @NotBlank(message = "Тип кузова не может быть пустым")
    @Column(name = "type_kuzov")
    private String bodyType;

    @Column(name = "description")
    private String description;

    @Column(name = "mileage")
    @Min(value = 0, message = "Пробег должен быть положительным")
    private Integer mileage;

    @Column(name = "year")
    @Min(value = 1900, message = "Год должен быть не раньше 1900")
    @Max(value = 2100, message = "Год должен быть не позже 2100")
    private Integer year;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "id_chelovek")
    private Customer customer;

    @Column(name = "condition")
    private String condition;

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