package com.example.javacrm.controller;

import com.example.javacrm.model.Customer;
import com.example.javacrm.service.CustomerService;
import com.example.javacrm.service.DatabaseService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;

import java.io.IOException;
import java.util.Optional;

public class CustomersController {
    @FXML private TableView<Customer> customersTable;
    @FXML private TableColumn<Customer, Long> idColumn;
    @FXML private TableColumn<Customer, String> lastNameColumn;
    @FXML private TableColumn<Customer, String> firstNameColumn;
    @FXML private TableColumn<Customer, String> middleNameColumn;
    @FXML private TableColumn<Customer, String> maritalStatusColumn;
    @FXML private TableColumn<Customer, Integer> childrenCountColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, String> passportColumn;
    @FXML private TableColumn<Customer, String> registrationDateColumn;
    @FXML private TableColumn<Customer, Void> actionsColumn;

    @FXML private TextField lastNameFilterField;
    @FXML private TextField firstNameFilterField;
    @FXML private TextField middleNameFilterField;
    @FXML private TextField emailFilterField;
    @FXML private TextField passportFilterField;
    @FXML private TextField phoneFilterField;

    private CustomerService customerService;

    @FXML
    public void initialize() {
        customerService = new CustomerService(DatabaseService.getInstance());
        
        // Инициализация колонок таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        middleNameColumn.setCellValueFactory(new PropertyValueFactory<>("middleName"));
        maritalStatusColumn.setCellValueFactory(new PropertyValueFactory<>("maritalStatus"));
        childrenCountColumn.setCellValueFactory(new PropertyValueFactory<>("childrenCount"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        passportColumn.setCellValueFactory(new PropertyValueFactory<>("passport"));
        registrationDateColumn.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        
        // Добавление кнопок действий
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Редактировать");
            private final Button deleteButton = new Button("Удалить");
            
            {
                editButton.setOnAction(e -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    handleEditCustomer(customer);
                });
                
                deleteButton.setOnAction(e -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    handleDeleteCustomer(customer);
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

    @FXML
    private void handleAddCustomer() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customer-dialog.fxml"));
            GridPane dialogContent = loader.load();
            CustomerDialogController controller = loader.getController();
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Добавить клиента");
            dialog.initModality(Modality.APPLICATION_MODAL);
            
            DialogPane dialogPane = new DialogPane();
            dialogPane.setContent(dialogContent);
            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            dialog.setDialogPane(dialogPane);
            
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Customer customer = controller.getCustomer();
                customerService.addCustomer(customer);
                refreshTable();
            }
        } catch (IOException e) {
            showError("Ошибка при открытии диалога", e.getMessage());
        }
    }

    private void handleEditCustomer(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customer-dialog.fxml"));
            GridPane dialogContent = loader.load();
            CustomerDialogController controller = loader.getController();
            controller.setCustomer(customer);
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Редактировать клиента");
            dialog.initModality(Modality.APPLICATION_MODAL);
            
            DialogPane dialogPane = new DialogPane();
            dialogPane.setContent(dialogContent);
            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            dialog.setDialogPane(dialogPane);
            
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Customer updatedCustomer = controller.getCustomer();
                customerService.updateCustomer(updatedCustomer);
                refreshTable();
            }
        } catch (IOException e) {
            showError("Ошибка при открытии диалога", e.getMessage());
        }
    }

    private void handleDeleteCustomer(Customer customer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление клиента");
        alert.setContentText("Вы уверены, что хотите удалить этого клиента?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            customerService.deleteCustomer(customer.getId());
            refreshTable();
        }
    }

    @FXML
    private void handleSearch() {
        // TODO: Реализовать поиск
    }

    @FXML
    private void handleClearSearch() {
        lastNameFilterField.clear();
        firstNameFilterField.clear();
        middleNameFilterField.clear();
        emailFilterField.clear();
        passportFilterField.clear();
        phoneFilterField.clear();
        refreshTable();
    }

    private void refreshTable() {
        customersTable.getItems().clear();
        customersTable.getItems().addAll(customerService.getAllCustomers());
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 