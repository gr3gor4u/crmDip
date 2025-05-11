package com.example.javacrm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "deals")
public class Deal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dogovor")
    private Long id;

    @NotNull(message = "Дата не может быть пустой")
    @Column(name = "data")
    private LocalDateTime date;

    @NotNull(message = "Цена не может быть пустой")
    @DecimalMin(value = "0.0", message = "Цена должна быть положительной")
    @Column(name = "price")
    private BigDecimal price;

    @NotNull(message = "Клиент не может быть пустым")
    @ManyToOne
    @JoinColumn(name = "id_chelovek")
    private Customer customer;

    @NotNull(message = "Автомобиль не может быть пустым")
    @OneToOne
    @JoinColumn(name = "vin_number")
    private Car car;

    @OneToOne
    @JoinColumn(name = "vin_number_trade")
    private TradeInCar tradeInCar;

    @OneToMany(mappedBy = "deal", cascade = CascadeType.ALL)
    private List<DealAdditionalEquipment> additionalEquipment;

    @NotNull(message = "Первоначальный взнос не может быть пустым")
    @DecimalMin(value = "0.0", message = "Первоначальный взнос должен быть положительным")
    @Column(name = "down_payment")
    private BigDecimal downPayment;

    @Column(name = "notes")
    private String notes;

    @NotNull(message = "Статус не может быть пустым")
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DealStatus status;

    @Column(name = "test_drive_date")
    private LocalDateTime testDriveDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (date == null) {
            date = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }

    public BigDecimal getAmount() { return price; }
    public void setAmount(BigDecimal amount) { this.price = amount; }

    public BigDecimal getDownPayment() { return downPayment; }
    public void setDownPayment(BigDecimal downPayment) { this.downPayment = downPayment; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
} 
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
} 