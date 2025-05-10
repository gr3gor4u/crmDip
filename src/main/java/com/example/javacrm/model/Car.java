package com.example.javacrm.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false, unique = true)
    private String vin;

    @Column(nullable = false)
    private Integer year;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarStatus status;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "mileage")
    private Integer mileage;

    @Column(name = "last_service_date")
    private LocalDateTime lastServiceDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum CarStatus {
        NEW,
        USED,
        SOLD,
        IN_SERVICE,
        RESERVED
    }

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