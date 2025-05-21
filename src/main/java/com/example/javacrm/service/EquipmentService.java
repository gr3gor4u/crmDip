package com.example.javacrm.service;

import com.example.javacrm.model.AdditionalEquipment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EquipmentService {
    private final DatabaseService databaseService;
    
    public EquipmentService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }
    
    public ObservableList<AdditionalEquipment> getAllEquipment() {
        List<AdditionalEquipment> equipment = databaseService.getAllEquipment();
        return FXCollections.observableArrayList(equipment);
    }
    
    public AdditionalEquipment getEquipmentById(Long id) {
        Optional<AdditionalEquipment> equipment = databaseService.getEquipmentById(id);
        return equipment.orElse(null);
    }
    
    public AdditionalEquipment createEquipment(String name, BigDecimal price, Integer quantity) {
        AdditionalEquipment equipment = new AdditionalEquipment();
        equipment.setName(name);
        equipment.setPrice(price);
        equipment.setQuantity(quantity);
        equipment.setAvailable(quantity > 0);
        return databaseService.saveEquipment(equipment);
    }
    
    public AdditionalEquipment updateEquipment(Long id, String name, BigDecimal price, Integer quantity) {
        Optional<AdditionalEquipment> equipmentOpt = databaseService.getEquipmentById(id);
        if (equipmentOpt.isPresent()) {
            AdditionalEquipment equipment = equipmentOpt.get();
            equipment.setName(name);
            equipment.setPrice(price);
            equipment.setQuantity(quantity);
            equipment.setAvailable(quantity > 0);
            databaseService.updateEquipment(equipment);
            return equipment;
        }
        return null;
    }
    
    public void deleteEquipment(Long id) {
        databaseService.deleteEquipment(id);
    }
    
    public ObservableList<AdditionalEquipment> searchEquipment(String name, BigDecimal minPrice, BigDecimal maxPrice) {
        List<AdditionalEquipment> equipment = databaseService.searchEquipment(name, minPrice, maxPrice);
        return FXCollections.observableArrayList(equipment);
    }
} 