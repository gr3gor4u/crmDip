package com.example.javacrm.controller;

import com.example.javacrm.model.Car;
import com.example.javacrm.model.ServiceRecord;
import com.example.javacrm.service.DealershipService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.beans.property.SimpleStringProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class ServiceController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private DatePicker startDateFilter;
    @FXML private DatePicker endDateFilter;
    
    @FXML private TableView<ServiceRecord> serviceTable;
    @FXML private TableColumn<ServiceRecord, Long> idColumn;
    @FXML private TableColumn<ServiceRecord, String> carColumn;
    @FXML private TableColumn<ServiceRecord, String> serviceTypeColumn;
    @FXML private TableColumn<ServiceRecord, String> descriptionColumn;
    @FXML private TableColumn<ServiceRecord, String> costColumn;
    @FXML private TableColumn<ServiceRecord, String> statusColumn;
    @FXML private TableColumn<ServiceRecord, String> startDateColumn;
    @FXML private TableColumn<ServiceRecord, String> completionDateColumn;
    @FXML private TableColumn<ServiceRecord, Void> actionsColumn;

    @FXML private Dialog<ServiceRecord> serviceDialog;
    @FXML private ComboBox<Long> carComboBox;
    @FXML private ComboBox<String> serviceTypeComboBox;
    @FXML private TextArea descriptionField;
    @FXML private TextField costField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker completionDatePicker;

    private final DealershipService dealershipService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Autowired
    public ServiceController(DealershipService dealershipService) {
        this.dealershipService = dealershipService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupActionsColumn();
        loadServiceRecords();
        setupFilters();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        carColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(
                cellData.getValue().getCar().getBrand() + " " + 
                cellData.getValue().getCar().getModel()
            )
        );
        serviceTypeColumn.setCellValueFactory(new PropertyValueFactory<>("serviceType"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        costColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatCurrency(cellData.getValue().getCost()))
        );
        statusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus().toString())
        );
        startDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(
                cellData.getValue().getStartDate().format(dateFormatter)
            )
        );
        completionDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(
                cellData.getValue().getCompletionDate() != null ? 
                cellData.getValue().getCompletionDate().format(dateFormatter) : 
                "В процессе"
            )
        );
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(createActionButtonsCellFactory());
    }

    private Callback<TableColumn<ServiceRecord, Void>, TableCell<ServiceRecord, Void>> createActionButtonsCellFactory() {
        return param -> new TableCell<>() {
            private final Button editButton = new Button("✎");
            private final Button deleteButton = new Button("✕");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    ServiceRecord record = getTableView().getItems().get(getIndex());
                    showEditServiceDialog(record);
                });

                deleteButton.setOnAction(event -> {
                    ServiceRecord record = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(record);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        };
    }

    private void setupFilters() {
        statusFilter.setItems(FXCollections.observableArrayList(
            "SCHEDULED", "IN_PROGRESS", "COMPLETED", "CANCELLED"
        ));
    }

    @FXML
    private void loadServiceRecords() {
        List<ServiceRecord> records = dealershipService.getAllServiceRecords();
        serviceTable.setItems(FXCollections.observableArrayList(records));
    }

    @FXML
    private void searchServiceRecords() {
        String searchQuery = searchField.getText();
        String selectedStatus = statusFilter.getValue();
        LocalDateTime startDate = startDateFilter.getValue() != null ? 
            startDateFilter.getValue().atStartOfDay() : null;
        LocalDateTime endDate = endDateFilter.getValue() != null ? 
            endDateFilter.getValue().atTime(23, 59, 59) : null;

        List<ServiceRecord> records = dealershipService.searchServiceRecords(
            searchQuery, selectedStatus, startDate, endDate
        );
        serviceTable.setItems(FXCollections.observableArrayList(records));
    }

    @FXML
    private void resetSearch() {
        searchField.clear();
        statusFilter.setValue(null);
        startDateFilter.setValue(null);
        endDateFilter.setValue(null);
        loadServiceRecords();
    }

    @FXML
    private void showAddServiceDialog() {
        clearDialogFields();
        setupDialogComboBoxes();
        serviceDialog.setTitle("Добавить сервисную запись");
        
        serviceDialog.showAndWait().ifPresent(record -> {
            dealershipService.addServiceRecord(record);
            loadServiceRecords();
        });
    }

    private void showEditServiceDialog(ServiceRecord record) {
        fillDialogFields(record);
        setupDialogComboBoxes();
        serviceDialog.setTitle("Редактировать сервисную запись");
        
        serviceDialog.showAndWait().ifPresent(updatedRecord -> {
            updatedRecord.setId(record.getId());
            dealershipService.updateServiceRecord(updatedRecord);
            loadServiceRecords();
        });
    }

    private void showDeleteConfirmation(ServiceRecord record) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление сервисной записи");
        alert.setContentText("Вы уверены, что хотите удалить сервисную запись #" + record.getId() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dealershipService.deleteServiceRecord(record.getId());
                loadServiceRecords();
            }
        });
    }

    private void setupDialogComboBoxes() {
        // Заполняем комбобокс автомобилями
        carComboBox.setItems(FXCollections.observableArrayList(
            dealershipService.getAllCars().stream()
                .map(Car::getId)
                .toList()
        ));

        // Заполняем комбобокс типами сервиса
        serviceTypeComboBox.setItems(FXCollections.observableArrayList(
            "ТО", "Ремонт", "Диагностика", "Замена деталей", "Другое"
        ));

        // Заполняем комбобокс статусами
        statusComboBox.setItems(FXCollections.observableArrayList(
            "SCHEDULED", "IN_PROGRESS", "COMPLETED", "CANCELLED"
        ));
    }

    private void clearDialogFields() {
        carComboBox.setValue(null);
        serviceTypeComboBox.setValue(null);
        descriptionField.clear();
        costField.clear();
        statusComboBox.setValue(null);
        startDatePicker.setValue(null);
        completionDatePicker.setValue(null);
    }

    private void fillDialogFields(ServiceRecord record) {
        carComboBox.setValue(record.getCar().getId());
        serviceTypeComboBox.setValue(record.getServiceType());
        descriptionField.setText(record.getDescription());
        costField.setText(record.getCost().toString());
        statusComboBox.setValue(record.getStatus().toString());
        startDatePicker.setValue(record.getStartDate().toLocalDate());
        if (record.getCompletionDate() != null) {
            completionDatePicker.setValue(record.getCompletionDate().toLocalDate());
        }
    }

    private ServiceRecord createServiceRecordFromDialog() {
        ServiceRecord record = new ServiceRecord();
        record.setCar(dealershipService.getCarById(carComboBox.getValue()).orElseThrow(() -> new RuntimeException("Car not found")));
        record.setServiceType(serviceTypeComboBox.getValue());
        record.setDescription(descriptionField.getText());
        record.setCost(new BigDecimal(costField.getText()));
        record.setStatus(ServiceRecord.Status.valueOf(statusComboBox.getValue()));
        record.setStartDate(startDatePicker.getValue().atStartOfDay());
        if (completionDatePicker.getValue() != null) {
            record.setCompletionDate(completionDatePicker.getValue().atTime(23, 59, 59));
        }
        return record;
    }

    private String formatCurrency(BigDecimal amount) {
        return String.format("%,.2f ₽", amount);
    }
} 