package com.example.javacrm.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class Deal {
    private Long id;
    private Long carId;
    private Long customerId;
    private Long managerId;
    private String insuranceNumber;
    private boolean noInsurance;
    private BigDecimal totalPrice;
    private String status;
    private LocalDate dealDate;
    private List<DealEquipment> equipment;
    private String notes;
    
    // References to related entities
    private Car car;
    private Customer customer;
    private User manager;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }
    public String getInsuranceNumber() { return insuranceNumber; }
    public void setInsuranceNumber(String insuranceNumber) { this.insuranceNumber = insuranceNumber; }
    public boolean isNoInsurance() { return noInsurance; }
    public void setNoInsurance(boolean noInsurance) { this.noInsurance = noInsurance; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDate getDealDate() { return dealDate; }
    public void setDealDate(LocalDate dealDate) { this.dealDate = dealDate; }
    public List<DealEquipment> getEquipment() { return equipment; }
    public void setEquipment(List<DealEquipment> equipment) { this.equipment = equipment; }
    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public User getManager() { return manager; }
    public void setManager(User manager) { this.manager = manager; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
} 