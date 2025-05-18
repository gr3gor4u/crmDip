package com.example.javacrm.controller;

import com.example.javacrm.model.Deal;
import com.example.javacrm.model.DealEquipment;
import com.example.javacrm.service.DealService;
import com.example.javacrm.service.CustomerService;
import com.example.javacrm.service.CarService;
import com.example.javacrm.service.InsuranceService;
import com.example.javacrm.service.EquipmentService;
import com.example.javacrm.service.DatabaseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;
import javafx.scene.layout.HBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DealsController {
    @FXML
    private TableView<Deal> dealsTable;
    @FXML
    private TableColumn<Deal, Long> idColumn;
    @FXML
    private TableColumn<Deal, String> customerColumn;
    @FXML
    private TableColumn<Deal, String> carColumn;
    @FXML
    private TableColumn<Deal, Double> amountColumn;
    @FXML
    private TableColumn<Deal, String> dateColumn;
    @FXML
    private TableColumn<Deal, String> statusColumn;
    @FXML
    private TableColumn<Deal, Void> actionsColumn;
    
    @FXML
    private TextField customerIdField;
    @FXML
    private TextField carIdField;
    @FXML
    private TextField insuranceIdField;
    @FXML
    private TextField totalPriceField;
    @FXML
    private TextField statusField;
    
    @FXML
    private TextField searchCustomerIdField;
    @FXML
    private TextField searchCarIdField;
    @FXML
    private TextField searchStatusField;
    
    private DealService dealService;
    private CustomerService customerService;
    private CarService carService;
    private InsuranceService insuranceService;
    private EquipmentService equipmentService;
    private ObservableList<Deal> dealsList;
    private Deal selectedDeal;
    
    @FXML
    public void initialize() {
        DatabaseService databaseService = DatabaseService.getInstance();
        customerService = new CustomerService(databaseService);
        carService = new CarService(databaseService);
        insuranceService = new InsuranceService(databaseService, customerService, carService);
        equipmentService = new EquipmentService(databaseService);
        dealService = new DealService(databaseService, customerService, carService, insuranceService, equipmentService);
        
        // Инициализация колонок таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        carColumn.setCellValueFactory(new PropertyValueFactory<>("car"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        
        // Добавление кнопок действий
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Редактировать");
            private final Button deleteButton = new Button("Удалить");
            
            {
                editButton.setOnAction(e -> {
                    Deal deal = getTableView().getItems().get(getIndex());
                    handleEditDeal(deal);
                });
                
                deleteButton.setOnAction(e -> {
                    Deal deal = getTableView().getItems().get(getIndex());
                    if (deal != null) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Подтверждение удаления");
                        alert.setHeaderText("Удаление сделки");
                        alert.setContentText("Вы уверены, что хотите удалить эту сделку?");
                        
                        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                            dealService.deleteDeal(deal.getId());
                            refreshTable();
                        }
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });
        
        refreshTable();
    }
    
    private void refreshTable() {
        dealsTable.getItems().clear();
        dealsTable.getItems().addAll(dealService.getAllDeals());
    }
    
    private void populateFields(Deal deal) {
        customerIdField.setText(String.valueOf(deal.getCustomerId()));
        carIdField.setText(String.valueOf(deal.getCarId()));
        insuranceIdField.setText(String.valueOf(deal.getInsuranceId()));
        totalPriceField.setText(String.valueOf(deal.getTotalPrice()));
        statusField.setText(deal.getStatus());
    }
    
    private void clearFields() {
        customerIdField.clear();
        carIdField.clear();
        insuranceIdField.clear();
        totalPriceField.clear();
        statusField.clear();
        selectedDeal = null;
    }
    
    @FXML
    private void handleAddDeal() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText("Функционал добавления сделки будет реализован позже");
        alert.showAndWait();
    }
    
    @FXML
    private void handleUpdateDeal() {
        if (selectedDeal == null) {
            showError("No deal selected");
            return;
        }
        
        try {
            if (!customerIdField.getText().isEmpty()) {
                selectedDeal.setCustomerId(Long.parseLong(customerIdField.getText()));
            }
            if (!carIdField.getText().isEmpty()) {
                selectedDeal.setCarId(Long.parseLong(carIdField.getText()));
            }
            if (!insuranceIdField.getText().isEmpty()) {
                selectedDeal.setInsuranceId(Long.parseLong(insuranceIdField.getText()));
            }
            selectedDeal.setTotalPrice(Double.parseDouble(totalPriceField.getText()));
            selectedDeal.setStatus(statusField.getText());
            
            dealService.updateDeal(selectedDeal);
            refreshTable();
            clearFields();
        } catch (NumberFormatException e) {
            showError("Invalid price or ID format");
        } catch (Exception e) {
            showError("Failed to update deal: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSearch() {
        String customerId = searchCustomerIdField.getText();
        String carId = searchCarIdField.getText();
        String status = searchStatusField.getText();
        
        dealsTable.getItems().clear();
        dealsTable.getItems().addAll(dealService.searchDeals(customerId, carId, status));
    }
    
    @FXML
    private void handleClearSearch() {
        searchCustomerIdField.clear();
        searchCarIdField.clear();
        searchStatusField.clear();
        refreshTable();
    }
    
    private void handleEditDeal(Deal deal) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText("Функционал редактирования сделки будет реализован позже");
        alert.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 