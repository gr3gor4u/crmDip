package com.example.javacrm.controller;

import com.example.javacrm.model.AdditionalEquipment;
import com.example.javacrm.service.EquipmentService;
import com.example.javacrm.service.DatabaseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class EquipmentController {
    @FXML
    private TableView<AdditionalEquipment> equipmentTable;
    @FXML
    private TableColumn<AdditionalEquipment, Long> idColumn;
    @FXML
    private TableColumn<AdditionalEquipment, String> nameColumn;
    @FXML
    private TableColumn<AdditionalEquipment, String> descriptionColumn;
    @FXML
    private TableColumn<AdditionalEquipment, Double> priceColumn;
    @FXML
    private TableColumn<AdditionalEquipment, Void> actionsColumn;
    
    @FXML
    private TextField nameField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField typeField;
    @FXML
    private TextField statusField;
    
    @FXML
    private TextField searchNameField;
    @FXML
    private TextField searchTypeField;
    @FXML
    private TextField searchStatusField;
    
    private EquipmentService equipmentService;
    private ObservableList<AdditionalEquipment> equipmentList;
    private AdditionalEquipment selectedEquipment;
    
    @FXML
    public void initialize() {
        equipmentService = new EquipmentService(DatabaseService.getInstance());
        
        // Инициализация колонок таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        // Загрузка данных
        refreshTable();
    }
    
    private void refreshTable() {
        equipmentTable.getItems().clear();
        equipmentTable.getItems().addAll(equipmentService.getAllEquipment());
    }
    
    private void populateFields(AdditionalEquipment equipment) {
        nameField.setText(equipment.getName());
        descriptionField.setText(equipment.getDescription());
        priceField.setText(String.valueOf(equipment.getPrice()));
        typeField.setText(equipment.getType());
        statusField.setText(equipment.getStatus());
    }
    
    private void clearFields() {
        nameField.clear();
        descriptionField.clear();
        priceField.clear();
        typeField.clear();
        statusField.clear();
        selectedEquipment = null;
    }
    
    @FXML
    private void handleAddEquipment() {
        // TODO: Реализовать добавление оборудования
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText("Функция добавления оборудования будет реализована позже");
        alert.showAndWait();
    }
    
    @FXML
    private void handleUpdateEquipment() {
        if (selectedEquipment == null) {
            showError("No equipment selected");
            return;
        }
        
        try {
            selectedEquipment.setName(nameField.getText());
            selectedEquipment.setDescription(descriptionField.getText());
            selectedEquipment.setPrice(Double.parseDouble(priceField.getText()));
            selectedEquipment.setType(typeField.getText());
            selectedEquipment.setStatus(statusField.getText());
            
            equipmentService.updateEquipment(selectedEquipment);
            refreshTable();
            clearFields();
        } catch (NumberFormatException e) {
            showError("Invalid price format");
        } catch (Exception e) {
            showError("Failed to update equipment: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleDeleteEquipment() {
        if (selectedEquipment == null) {
            showError("No equipment selected");
            return;
        }
        
        try {
            equipmentService.deleteEquipment(selectedEquipment.getId());
            refreshTable();
            clearFields();
        } catch (Exception e) {
            showError("Failed to delete equipment: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSearch() {
        String name = searchNameField.getText();
        String type = searchTypeField.getText();
        String status = searchStatusField.getText();
        
        equipmentTable.getItems().clear();
        equipmentTable.getItems().addAll(equipmentService.searchEquipment(name, type, status));
    }
    
    @FXML
    private void handleClearSearch() {
        searchNameField.clear();
        searchTypeField.clear();
        searchStatusField.clear();
        refreshTable();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 