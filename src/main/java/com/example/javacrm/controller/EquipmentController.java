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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.control.cell.CheckBoxTableCell;

public class EquipmentController {
    @FXML
    private TableView<AdditionalEquipment> equipmentTable;
    @FXML
    private TableColumn<AdditionalEquipment, Long> idColumn;
    @FXML
    private TableColumn<AdditionalEquipment, String> nameColumn;
    @FXML
    private TableColumn<AdditionalEquipment, Double> priceColumn;
    @FXML
    private TableColumn<AdditionalEquipment, Integer> quantityColumn;
    @FXML
    private TableColumn<AdditionalEquipment, Boolean> availableColumn;
    @FXML
    private TableColumn<AdditionalEquipment, Void> actionsColumn;
    @FXML
    private TableColumn<AdditionalEquipment, Boolean> selectColumn;
    
    @FXML
    private TextField nameField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField quantityField;
    
    @FXML
    private TextField searchNameField;
    
    @FXML
    private TextField idFilterField;
    @FXML
    private TextField nameFilterField;
    @FXML
    private TextField minPriceFilterField;
    @FXML
    private TextField maxPriceFilterField;
    
    private EquipmentService equipmentService;
    private ObservableList<AdditionalEquipment> equipmentList;
    private AdditionalEquipment selectedEquipment;
    
    @FXML
    public void initialize() {
        equipmentService = new EquipmentService(DatabaseService.getInstance());
        
        // Инициализация колонок таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));
        
        // Колонка с чекбоксами для массового удаления
        selectColumn.setCellFactory(CheckBoxTableCell.forTableColumn(selectColumn));
        
        // Колонка с кнопками действий
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Редактировать");
            private final Button deleteButton = new Button("Удалить");
            
            {
                editButton.setOnAction(e -> {
                    AdditionalEquipment equipment = getTableView().getItems().get(getIndex());
                    handleEditEquipment(equipment);
                });
                
                deleteButton.setOnAction(e -> {
                    AdditionalEquipment equipment = getTableView().getItems().get(getIndex());
                    handleDeleteEquipment(equipment);
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
        equipmentList = equipmentService.getAllEquipment();
        equipmentTable.setItems(equipmentList);
    }
    
    private void populateFields(AdditionalEquipment equipment) {
        nameField.setText(equipment.getName());
        priceField.setText(String.valueOf(equipment.getPrice()));
        quantityField.setText(String.valueOf(equipment.getQuantity()));
    }
    
    private void clearFields() {
        nameField.clear();
        priceField.clear();
        quantityField.clear();
        selectedEquipment = null;
    }
    
    @FXML
    private void handleAddEquipment() {
        try {
            AdditionalEquipment equipment = new AdditionalEquipment();
            equipment.setName(nameField.getText());
            equipment.setPrice(Double.parseDouble(priceField.getText()));
            equipment.setQuantity(Integer.parseInt(quantityField.getText()));
            equipment.setAvailable(equipment.getQuantity() > 0);
            
            equipmentService.addEquipment(equipment);
            refreshTable();
            clearFields();
        } catch (NumberFormatException e) {
            showError("Неверный формат цены или количества");
        } catch (Exception e) {
            showError("Ошибка при добавлении оборудования: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleEditEquipment(AdditionalEquipment equipment) {
        selectedEquipment = equipment;
        populateFields(equipment);
    }
    
    @FXML
    private void handleUpdateEquipment() {
        if (selectedEquipment == null) {
            showError("Оборудование не выбрано");
            return;
        }
        
        try {
            selectedEquipment.setName(nameField.getText());
            selectedEquipment.setPrice(Double.parseDouble(priceField.getText()));
            selectedEquipment.setQuantity(Integer.parseInt(quantityField.getText()));
            selectedEquipment.setAvailable(selectedEquipment.getQuantity() > 0);
            
            equipmentService.updateEquipment(selectedEquipment);
            refreshTable();
            clearFields();
        } catch (NumberFormatException e) {
            showError("Неверный формат цены или количества");
        } catch (Exception e) {
            showError("Ошибка при обновлении оборудования: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleDeleteEquipment(AdditionalEquipment equipment) {
        equipmentService.deleteEquipment(equipment.getId());
        refreshTable();
    }
    
    @FXML
    private void handleMassDelete() {
        ObservableList<AdditionalEquipment> selectedItems = FXCollections.observableArrayList();
        for (AdditionalEquipment equipment : equipmentList) {
            if (equipmentTable.getSelectionModel().isSelected(equipmentTable.getItems().indexOf(equipment))) {
                selectedItems.add(equipment);
            }
        }
        
        for (AdditionalEquipment equipment : selectedItems) {
            equipmentService.deleteEquipment(equipment.getId());
        }
        refreshTable();
    }
    
    @FXML
    private void handleSearch() {
        String id = idFilterField.getText().trim();
        String name = nameFilterField.getText().trim();
        String minPrice = minPriceFilterField.getText().trim();
        String maxPrice = maxPriceFilterField.getText().trim();

        equipmentTable.getItems().clear();
        equipmentTable.getItems().addAll(equipmentService.searchEquipment(id, name, minPrice, maxPrice));
    }
    
    @FXML
    private void handleClearSearch() {
        idFilterField.clear();
        nameFilterField.clear();
        minPriceFilterField.clear();
        maxPriceFilterField.clear();
        refreshTable();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 