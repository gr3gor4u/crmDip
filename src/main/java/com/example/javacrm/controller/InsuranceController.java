package com.example.javacrm.controller;

import com.example.javacrm.model.Insurance;
import com.example.javacrm.service.InsuranceService;
import com.example.javacrm.service.CustomerService;
import com.example.javacrm.service.CarService;
import com.example.javacrm.service.DatabaseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class InsuranceController {
    @FXML
    private TableView<Insurance> insuranceTable;
    @FXML
    private TableColumn<Insurance, Long> idColumn;
    @FXML
    private TableColumn<Insurance, String> customerIdColumn;
    @FXML
    private TableColumn<Insurance, String> carVinColumn;
    @FXML
    private TableColumn<Insurance, String> insuranceTypeColumn;
    @FXML
    private TableColumn<Insurance, String> insuranceNumberColumn;
    @FXML
    private TableColumn<Insurance, String> statusColumn;
    @FXML
    private TableColumn<Insurance, LocalDateTime> createdAtColumn;
    
    @FXML
    private TextField customerIdField;
    @FXML
    private TextField carVinField;
    @FXML
    private TextField insuranceTypeField;
    @FXML
    private TextField insuranceNumberField;
    @FXML
    private TextField statusField;
    
    @FXML
    private TextField searchCustomerIdField;
    @FXML
    private TextField searchCarVinField;
    @FXML
    private TextField searchInsuranceTypeField;
    
    private InsuranceService insuranceService;
    private CustomerService customerService;
    private CarService carService;
    private ObservableList<Insurance> insuranceList;
    private Insurance selectedInsurance;
    
    @FXML
    public void initialize() {
        try {
            // Initialize services
            DatabaseService databaseService = DatabaseService.getInstance();
            customerService = new CustomerService(databaseService);
            carService = new CarService(databaseService);
            insuranceService = new InsuranceService(databaseService, customerService, carService);

            // Initialize table columns
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            customerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
            carVinColumn.setCellValueFactory(new PropertyValueFactory<>("carVin"));
            insuranceTypeColumn.setCellValueFactory(new PropertyValueFactory<>("insuranceType"));
            insuranceNumberColumn.setCellValueFactory(new PropertyValueFactory<>("insuranceNumber"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
            createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
            
            // Set date format for createdAt column
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            createdAtColumn.setCellFactory(column -> new TableCell<>() {
                @Override
                protected void updateItem(LocalDateTime date, boolean empty) {
                    super.updateItem(date, empty);
                    if (empty || date == null) {
                        setText(null);
                    } else {
                        setText(formatter.format(date));
                    }
                }
            });
            
            // Initialize insurance list
            insuranceList = FXCollections.observableArrayList();
            insuranceTable.setItems(insuranceList);
            
            // Add selection listener
            insuranceTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedInsurance = newSelection;
                    populateFields(selectedInsurance);
                }
            });

            // Load initial data
            refreshInsuranceList();
        } catch (Exception e) {
            showError("Failed to initialize: " + e.getMessage());
        }
    }
    
    public void setServices(InsuranceService insuranceService, CustomerService customerService, CarService carService) {
        this.insuranceService = insuranceService;
        this.customerService = customerService;
        this.carService = carService;
        refreshInsuranceList();
    }
    
    private void refreshInsuranceList() {
        insuranceList.clear();
        insuranceList.addAll(insuranceService.getAllInsurances());
    }
    
    private void populateFields(Insurance insurance) {
        customerIdField.setText(String.valueOf(insurance.getCustomerId()));
        carVinField.setText(insurance.getCarVin());
        insuranceTypeField.setText(insurance.getInsuranceType());
        insuranceNumberField.setText(insurance.getInsuranceNumber());
        statusField.setText(insurance.getStatus());
    }
    
    private void clearFields() {
        customerIdField.clear();
        carVinField.clear();
        insuranceTypeField.clear();
        insuranceNumberField.clear();
        statusField.clear();
        selectedInsurance = null;
    }
    
    @FXML
    private void handleAddInsurance() {
        try {
            Insurance insurance = new Insurance();
            if (!customerIdField.getText().isEmpty()) {
                insurance.setCustomerId(Long.parseLong(customerIdField.getText()));
            }
            insurance.setCarVin(carVinField.getText());
            insurance.setInsuranceType(insuranceTypeField.getText());
            insurance.setInsuranceNumber(insuranceNumberField.getText());
            insurance.setStatus(statusField.getText());
            
            insuranceService.addInsurance(insurance);
            refreshInsuranceList();
            clearFields();
        } catch (Exception e) {
            showError("Failed to add insurance: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleUpdateInsurance() {
        if (selectedInsurance == null) {
            showError("No insurance selected");
            return;
        }
        
        try {
            if (!customerIdField.getText().isEmpty()) {
                selectedInsurance.setCustomerId(Long.parseLong(customerIdField.getText()));
            }
            selectedInsurance.setCarVin(carVinField.getText());
            selectedInsurance.setInsuranceType(insuranceTypeField.getText());
            selectedInsurance.setInsuranceNumber(insuranceNumberField.getText());
            selectedInsurance.setStatus(statusField.getText());
            
            insuranceService.updateInsurance(selectedInsurance);
            refreshInsuranceList();
            clearFields();
        } catch (Exception e) {
            showError("Failed to update insurance: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleDeleteInsurance() {
        if (selectedInsurance == null) {
            showError("No insurance selected");
            return;
        }
        
        try {
            insuranceService.deleteInsurance(selectedInsurance.getId());
            refreshInsuranceList();
            clearFields();
        } catch (Exception e) {
            showError("Failed to delete insurance: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSearch() {
        String customerId = searchCustomerIdField.getText();
        String carVin = searchCarVinField.getText();
        String insuranceType = searchInsuranceTypeField.getText();
        
        insuranceList.clear();
        insuranceList.addAll(insuranceService.searchInsurances(carVin, customerId, insuranceType));
    }
    
    @FXML
    private void handleClearSearch() {
        searchCustomerIdField.clear();
        searchCarVinField.clear();
        searchInsuranceTypeField.clear();
        refreshInsuranceList();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 