package com.example.javacrm.model;

import java.time.LocalDateTime;

public class Car {
    private Long id;
    private String vin;
    private String brand;
    private String model;
    private Integer year;
    private String color;
    private String kuzov;
    private Double obemDvig;
    private Integer horsePower;
    private Double price;
    private String status;
    private LocalDateTime createdAt;

    public Car() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getKuzov() {
        return kuzov;
    }

    public void setKuzov(String kuzov) {
        this.kuzov = kuzov;
    }

    public Double getObemDvig() {
        return obemDvig;
    }

    public void setObemDvig(Double obemDvig) {
        this.obemDvig = obemDvig;
    }

    public Integer getHorsePower() {
        return horsePower;
    }

    public void setHorsePower(Integer horsePower) {
        this.horsePower = horsePower;
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