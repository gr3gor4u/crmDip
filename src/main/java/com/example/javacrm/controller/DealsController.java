package com.example.javacrm.controller;

import com.example.javacrm.model.Deal;
import com.example.javacrm.model.DealStatus;
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
public class DealsController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private DatePicker startDateFilter;
    @FXML private DatePicker endDateFilter;
    
    @FXML private TableView<Deal> dealsTable;
    @FXML private TableColumn<Deal, Long> idColumn;
    @FXML private TableColumn<Deal, String> customerColumn;
    @FXML private TableColumn<Deal, String> carColumn;
    @FXML private TableColumn<Deal, String> amountColumn;
    @FXML private TableColumn<Deal, String> downPaymentColumn;
    @FXML private TableColumn<Deal, String> statusColumn;
    @FXML private TableColumn<Deal, String> dealDateColumn;
    @FXML private TableColumn<Deal, String> testDriveDateColumn;
    @FXML private TableColumn<Deal, Void> actionsColumn;

    @FXML private Dialog<Deal> dealDialog;
    @FXML private ComboBox<Long> customerComboBox;
    @FXML private ComboBox<String> carComboBox;
    @FXML private TextField amountField;
    @FXML private TextField downPaymentField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private DatePicker dealDatePicker;
    @FXML private DatePicker testDriveDatePicker;
    @FXML private TextArea notesField;

    private final DealershipService dealershipService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Autowired
    public DealsController(DealershipService dealershipService) {
        this.dealershipService = dealershipService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupActionsColumn();
        loadDeals();
        setupFilters();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(
                cellData.getValue().getCustomer().getFullName()
            )
        );
        carColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(
                cellData.getValue().getCar().getBrand() + " " + 
                cellData.getValue().getCar().getModel()
            )
        );
        amountColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatCurrency(cellData.getValue().getAmount()))
        );
        downPaymentColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatCurrency(cellData.getValue().getDownPayment()))
        );
        statusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus().toString())
        );
        dealDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(
                cellData.getValue().getDate().format(dateFormatter)
            )
        );
        testDriveDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(
                cellData.getValue().getTestDriveDate() != null ? 
                cellData.getValue().getTestDriveDate().format(dateFormatter) : 
                "Не назначено"
            )
        );
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(createActionButtonsCellFactory());
    }

    private Callback<TableColumn<Deal, Void>, TableCell<Deal, Void>> createActionButtonsCellFactory() {
        return param -> new TableCell<>() {
            private final Button editButton = new Button("✎");
            private final Button deleteButton = new Button("✕");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    Deal deal = getTableView().getItems().get(getIndex());
                    showEditDealDialog(deal);
                });

                deleteButton.setOnAction(event -> {
                    Deal deal = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(deal);
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
            "NEW", "TEST_DRIVE_SCHEDULED", "TEST_DRIVE_COMPLETED", "NEGOTIATION", "CONTRACT_PREPARATION", "COMPLETED", "CANCELLED"
        ));
    }

    @FXML
    private void loadDeals() {
        List<Deal> deals = dealershipService.getAllDeals();
        dealsTable.setItems(FXCollections.observableArrayList(deals));
    }

    @FXML
    private void searchDeals() {
        String searchQuery = searchField.getText();
        String selectedStatus = statusFilter.getValue();
        LocalDateTime startDate = startDateFilter.getValue() != null ? 
            startDateFilter.getValue().atStartOfDay() : null;
        LocalDateTime endDate = endDateFilter.getValue() != null ? 
            endDateFilter.getValue().atTime(23, 59, 59) : null;

        List<Deal> deals = dealershipService.searchDeals(
            searchQuery, selectedStatus, startDate, endDate
        );
        dealsTable.setItems(FXCollections.observableArrayList(deals));
    }

    @FXML
    private void resetSearch() {
        searchField.clear();
        statusFilter.setValue(null);
        startDateFilter.setValue(null);
        endDateFilter.setValue(null);
        loadDeals();
    }

    @FXML
    private void showAddDealDialog() {
        clearDialogFields();
        setupDialogComboBoxes();
        dealDialog.setTitle("Добавить сделку");
        
        dealDialog.showAndWait().ifPresent(deal -> {
            dealershipService.addDeal(deal);
            loadDeals();
        });
    }

    private void showEditDealDialog(Deal deal) {
        fillDialogFields(deal);
        setupDialogComboBoxes();
        dealDialog.setTitle("Редактировать сделку");
        
        dealDialog.showAndWait().ifPresent(updatedDeal -> {
            updatedDeal.setId(deal.getId());
            dealershipService.updateDeal(updatedDeal);
            loadDeals();
        });
    }

    private void showDeleteConfirmation(Deal deal) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление сделки");
        alert.setContentText("Вы уверены, что хотите удалить сделку #" + deal.getId() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dealershipService.deleteDeal(deal.getId());
                loadDeals();
            }
        });
    }

    private void setupDialogComboBoxes() {
        // Заполняем комбобоксы клиентами и автомобилями
        customerComboBox.setItems(FXCollections.observableArrayList(
            dealershipService.getAllCustomers().stream()
                .map(customer -> customer.getId())
                .toList()
        ));
        
        carComboBox.setItems(FXCollections.observableArrayList(
            dealershipService.getAvailableCars().stream()
                .map(car -> car.getVinNumber())
                .toList()
        ));

        statusComboBox.setItems(FXCollections.observableArrayList(
            "NEW", "IN_PROGRESS", "COMPLETED", "CANCELLED", "PENDING_PAYMENT"
        ));
    }

    private void clearDialogFields() {
        customerComboBox.setValue(null);
        carComboBox.setValue(null);
        amountField.clear();
        downPaymentField.clear();
        statusComboBox.setValue(null);
        dealDatePicker.setValue(null);
        testDriveDatePicker.setValue(null);
        notesField.clear();
    }

    private void fillDialogFields(Deal deal) {
        customerComboBox.setValue(deal.getCustomer().getId());
        carComboBox.setValue(deal.getCar().getVinNumber());
        amountField.setText(deal.getAmount().toString());
        downPaymentField.setText(deal.getDownPayment().toString());
        statusComboBox.setValue(deal.getStatus().toString());
        dealDatePicker.setValue(deal.getDate().toLocalDate());
        if (deal.getTestDriveDate() != null) {
            testDriveDatePicker.setValue(deal.getTestDriveDate().toLocalDate());
        }
        notesField.setText(deal.getNotes());
    }

    private Deal createDealFromDialog() {
        Deal deal = new Deal();
        deal.setCustomer(dealershipService.getCustomerById(customerComboBox.getValue()));
        deal.setCar(dealershipService.getCarByVin(carComboBox.getValue()));
        deal.setAmount(new BigDecimal(amountField.getText()));
        deal.setDownPayment(new BigDecimal(downPaymentField.getText()));
        deal.setStatus(DealStatus.valueOf(statusComboBox.getValue()));
        deal.setDate(dealDatePicker.getValue().atStartOfDay());
        if (testDriveDatePicker.getValue() != null) {
            deal.setTestDriveDate(testDriveDatePicker.getValue().atStartOfDay());
        }
        deal.setNotes(notesField.getText());
        return deal;
    }

    private String formatCurrency(BigDecimal amount) {
        return String.format("%,.2f ₽", amount);
    }
} 