package com.example.javacrm.model;

public class Car {
    private Long id;
    private String model;
    private String brand;
    private Integer carYear;
    private Double price;
    private String status;
    private String createdAt;

    public Car() {
    }

    public Car(Long id, String model, String brand, Integer carYear, Double price, String status) {
        this.id = id;
        this.model = model;
        this.brand = brand;
        this.carYear = carYear;
        this.price = price;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getCarYear() {
        return carYear;
    }

    public void setCarYear(Integer carYear) {
        this.carYear = carYear;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isAvailable() {
        return "AVAILABLE".equals(status);
    }

    public boolean isNotAvailable() {
        return "NOT_AVAILABLE".equals(status);
    }

    public boolean isSold() {
        return "SOLD".equals(status);
    }
} 