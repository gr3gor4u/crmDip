package com.example.javacrm.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "deals")
public class Deal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DealStatus status;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(name = "down_payment")
    private BigDecimal downPayment;

    @Column(name = "test_drive_date")
    private LocalDateTime testDriveDate;

    @Column(name = "deal_date")
    private LocalDateTime dealDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum DealStatus {
        LEAD,
        TEST_DRIVE_SCHEDULED,
        NEGOTIATION,
        PENDING_APPROVAL,
        APPROVED,
        COMPLETED,
        CANCELLED
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