package com.example.javacrm.service;

import com.example.javacrm.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class DealService {
    private final DatabaseService databaseService;
    private final CustomerService customerService;
    private final CarService carService;
    private final InsuranceService insuranceService;
    private final EquipmentService equipmentService;

    public DealService(DatabaseService databaseService, CustomerService customerService,
                      CarService carService, InsuranceService insuranceService,
                      EquipmentService equipmentService) {
        this.databaseService = databaseService;
        this.customerService = customerService;
        this.carService = carService;
        this.insuranceService = insuranceService;
        this.equipmentService = equipmentService;
    }

    public List<Deal> getAllDeals() {
        return databaseService.getAllDeals();
    }

    public Deal getDealById(Long id) {
        Optional<Deal> deal = databaseService.getDealById(id);
        return deal.orElse(null);
    }

    public Deal createDeal(Deal deal) {
        Long id = databaseService.saveDeal(deal);
        if (id != null) {
            return getDealById(id);
        }
        return null;
    }

    public void updateDeal(Deal deal) {
        databaseService.updateDeal(deal);
    }

    public void deleteDeal(Long id) {
        databaseService.deleteDeal(id);
    }

    public List<Deal> searchDeals(String customerName, String carModel, String status) {
        return databaseService.searchDeals(customerName, carModel, status);
    }

    public void addEquipmentToDeal(Long dealId, Long equipmentId, int quantity, BigDecimal price) {
        DealEquipment item = new DealEquipment();
        item.setDealId(dealId);
        item.setEquipmentId(equipmentId);
        item.setQuantity(quantity);
        item.setPrice(price);
        
        databaseService.saveDealEquipment(item);
    }

    public void updateDealEquipment(DealEquipment equipment) {
        databaseService.updateDealEquipment(equipment);
    }
} 