package com.example.javacrm.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Insurance {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty insuranceNumber = new SimpleStringProperty();
    private final LongProperty customerId = new SimpleLongProperty();
    private final StringProperty carVin = new SimpleStringProperty();
    private final ObjectProperty<InsuranceType> insuranceType = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> startDate = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDate> expiryDate = new SimpleObjectProperty<>();
    private final StringProperty status = new SimpleStringProperty();
    
    // Дополнительные поля для отображения
    private String customerFirstName;
    private String customerLastName;
    private String carBrand;
    private String carModel;

    public enum InsuranceType {
        КАСКО, ОСАГО
    }

    public Insurance() {
        this.status.set("Активна");
    }

    public long getId() { return id.get(); }
    public void setId(long id) { this.id.set(id); }
    public LongProperty idProperty() { return id; }

    public String getInsuranceNumber() { return insuranceNumber.get(); }
    public void setInsuranceNumber(String insuranceNumber) { this.insuranceNumber.set(insuranceNumber); }
    public StringProperty insuranceNumberProperty() { return insuranceNumber; }

    public long getCustomerId() { return customerId.get(); }
    public void setCustomerId(long customerId) { this.customerId.set(customerId); }
    public LongProperty customerIdProperty() { return customerId; }

    public String getCarVin() { return carVin.get(); }
    public void setCarVin(String carVin) { this.carVin.set(carVin); }
    public StringProperty carVinProperty() { return carVin; }

    public InsuranceType getInsuranceType() { return insuranceType.get(); }
    public void setInsuranceType(InsuranceType insuranceType) { this.insuranceType.set(insuranceType); }
    public ObjectProperty<InsuranceType> insuranceTypeProperty() { return insuranceType; }

    public LocalDate getStartDate() { return startDate.get(); }
    public void setStartDate(LocalDate startDate) { this.startDate.set(startDate); }
    public ObjectProperty<LocalDate> startDateProperty() { return startDate; }

    public LocalDate getExpiryDate() { return expiryDate.get(); }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate.set(expiryDate); }
    public ObjectProperty<LocalDate> expiryDateProperty() { return expiryDate; }

    public String getStatus() {
        if (expiryDate.get() != null && LocalDate.now().isAfter(expiryDate.get())) {
            return "Истекла";
        }
        return status.get();
    }
    public void setStatus(String status) { this.status.set(status); }
    public StringProperty statusProperty() { return status; }

    // Геттеры и сеттеры для дополнительных полей
    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    @Override
    public String toString() {
        return String.format("%s - %s", insuranceNumber.get(), insuranceType.get());
    }
} 