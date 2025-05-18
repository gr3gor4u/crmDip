package com.example.javacrm.model;

import java.time.LocalDateTime;

public class Insurance {
    private Long id;
    private String carVin;
    private Long customerId;
    private String insuranceType;
    private String insuranceNumber;
    private Double price;
    private String status;
    private LocalDateTime createdAt;
    
    // Relationships
    private Customer customer;
    private Car car;

    public Insurance() {
        this.createdAt = LocalDateTime.now();
    }

    public Insurance(Long id, String carVin, Long customerId, String insuranceType, String insuranceNumber, Double price, String status) {
        this.id = id;
        this.carVin = carVin;
        this.customerId = customerId;
        this.insuranceType = insuranceType;
        this.insuranceNumber = insuranceNumber;
        this.price = price;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCarVin() {
        return carVin;
    }

    public void setCarVin(String carVin) {
        this.carVin = carVin;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(String insuranceType) {
        this.insuranceType = insuranceType;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) {
            this.customerId = customer.getId();
        }
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
        if (car != null) {
            this.carVin = car.getVin();
        }
    }
} 