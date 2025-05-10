package com.example.javacrm.controller;

import com.example.javacrm.model.Customer;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class CustomersController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> tagFilter;
    @FXML private TableView<Customer> customersTable;
    @FXML private TableColumn<Customer, Long> idColumn;
    @FXML private TableColumn<Customer, String> firstNameColumn;
    @FXML private TableColumn<Customer, String> lastNameColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, String> tagsColumn;
    @FXML private TableColumn<Customer, String> createdAtColumn;
    @FXML private TableColumn<Customer, Void> actionsColumn;

    @FXML private Dialog<Customer> customerDialog;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField tagsField;
    @FXML private TextArea notesField;

    private final DealershipService dealershipService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Autowired
    public CustomersController(DealershipService dealershipService) {
        this.dealershipService = dealershipService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupActionsColumn();
        loadCustomers();
        setupTagFilter();
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        tagsColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(
                String.join(", ", cellData.getValue().getTags())
            )
        );
        createdAtColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(
                cellData.getValue().getCreatedAt().format(dateFormatter)
            )
        );
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(createActionButtonsCellFactory());
    }

    private Callback<TableColumn<Customer, Void>, TableCell<Customer, Void>> createActionButtonsCellFactory() {
        return param -> new TableCell<>() {
            private final Button editButton = new Button("✎");
            private final Button deleteButton = new Button("✕");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    showEditCustomerDialog(customer);
                });

                deleteButton.setOnAction(event -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(customer);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        };
    }

    private void setupTagFilter() {
        List<Customer> customers = dealershipService.getAllCustomers();
        Set<String> allTags = new HashSet<>();
        customers.forEach(customer -> allTags.addAll(customer.getTags()));
        tagFilter.setItems(FXCollections.observableArrayList(allTags));
    }

    @FXML
    private void loadCustomers() {
        List<Customer> customers = dealershipService.getAllCustomers();
        customersTable.setItems(FXCollections.observableArrayList(customers));
    }

    @FXML
    private void searchCustomers() {
        String searchQuery = searchField.getText();
        String selectedTag = tagFilter.getValue();

        List<Customer> customers;
        if (selectedTag != null && !selectedTag.isEmpty()) {
            customers = dealershipService.searchCustomersByTag(selectedTag);
        } else {
            customers = dealershipService.searchCustomers(searchQuery);
        }

        customersTable.setItems(FXCollections.observableArrayList(customers));
    }

    @FXML
    private void resetSearch() {
        searchField.clear();
        tagFilter.setValue(null);
        loadCustomers();
    }

    @FXML
    private void showAddCustomerDialog() {
        clearDialogFields();
        customerDialog.setTitle("Добавить клиента");
        
        customerDialog.showAndWait().ifPresent(customer -> {
            dealershipService.addCustomer(customer);
            loadCustomers();
            setupTagFilter();
        });
    }

    private void showEditCustomerDialog(Customer customer) {
        fillDialogFields(customer);
        customerDialog.setTitle("Редактировать клиента");
        
        customerDialog.showAndWait().ifPresent(updatedCustomer -> {
            updatedCustomer.setId(customer.getId());
            dealershipService.updateCustomer(updatedCustomer);
            loadCustomers();
            setupTagFilter();
        });
    }

    private void showDeleteConfirmation(Customer customer) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление клиента");
        alert.setContentText("Вы уверены, что хотите удалить клиента " + 
                           customer.getFirstName() + " " + customer.getLastName() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dealershipService.deleteCustomer(customer.getId());
                loadCustomers();
                setupTagFilter();
            }
        });
    }

    private void clearDialogFields() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        tagsField.clear();
        notesField.clear();
    }

    private void fillDialogFields(Customer customer) {
        firstNameField.setText(customer.getFirstName());
        lastNameField.setText(customer.getLastName());
        emailField.setText(customer.getEmail());
        phoneField.setText(customer.getPhone());
        tagsField.setText(String.join(", ", customer.getTags()));
        notesField.setText(customer.getNotes());
    }

    private Customer createCustomerFromDialog() {
        Customer customer = new Customer();
        customer.setFirstName(firstNameField.getText());
        customer.setLastName(lastNameField.getText());
        customer.setEmail(emailField.getText());
        customer.setPhone(phoneField.getText());
        customer.setNotes(notesField.getText());

        String[] tags = tagsField.getText().split(",");
        customer.setTags(new HashSet<>(Arrays.asList(tags)));

        return customer;
    }
} 