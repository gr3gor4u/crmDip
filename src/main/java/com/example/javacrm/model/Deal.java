package com.example.javacrm.model;

import java.time.LocalDate;

public class Deal {
    private Long id;
    private String customerName;
    private String carModel;
    private Double amount;
    private LocalDate date;

    public Deal() {
    }

    public Deal(Long id, String customerName, String carModel, Double amount, LocalDate date) {
        this.id = id;
        this.customerName = customerName;
        this.carModel = carModel;
        this.amount = amount;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
} 