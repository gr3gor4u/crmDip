package com.example.javacrm.controller;

import com.example.javacrm.model.Insurance;
import com.example.javacrm.model.Customer;
import com.example.javacrm.service.InsuranceService;
import com.example.javacrm.service.CustomerService;
import com.example.javacrm.service.CarService;
import com.example.javacrm.service.DatabaseService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class InsuranceDialogController {
    @FXML
    private ComboBox<String> carVinComboBox;
    @FXML
    private ComboBox<Customer> customerComboBox;
    @FXML
    private ComboBox<String> insuranceTypeComboBox;
    @FXML
    private TextField insuranceNumberField;
    @FXML
    private TextField priceField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private Button okButton;
    @FXML
    private Button cancelButton;

    private Insurance insurance;
    private boolean okClicked = false;
    private InsuranceService insuranceService;
    private CustomerService customerService;
    private CarService carService;

    @FXML
    private void initialize() {
        DatabaseService databaseService = DatabaseService.getInstance();
        customerService = new CustomerService(databaseService);
        carService = new CarService(databaseService);
        insuranceService = new InsuranceService(databaseService, customerService, carService);

        insuranceTypeComboBox.getItems().addAll("ОСАГО", "КАСКО", "ДСАГО");
        
        loadCustomers();
        loadCars();
        
        // Устанавливаем текущую дату как минимальную для startDatePicker
        startDatePicker.setValue(LocalDate.now());
        
        // Устанавливаем валидацию для priceField
        priceField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                priceField.setText(oldVal);
            }
        });
    }

    private void loadCustomers() {
        ObservableList<Customer> customers = FXCollections.observableArrayList(customerService.getAllCustomers());
        customerComboBox.setItems(customers);
        customerComboBox.setCellFactory(param -> new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer customer, boolean empty) {
                super.updateItem(customer, empty);
                if (empty || customer == null) {
                    setText(null);
                } else {
                    setText(customer.getFullName());
                }
            }
        });
    }

    private void loadCars() {
        carVinComboBox.setItems(carService.getAllCarVins());
    }

    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;

        if (insurance != null) {
            carVinComboBox.setValue(insurance.getCarVin());
            customerComboBox.setValue(customerService.getCustomerById(insurance.getCustomerId()));
            insuranceTypeComboBox.setValue(insurance.getInsuranceType());
            insuranceNumberField.setText(insurance.getInsuranceNumber());
            priceField.setText(String.valueOf(insurance.getPrice()));
            startDatePicker.setValue(insurance.getStartDate());
            endDatePicker.setValue(insurance.getEndDate());
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            insurance.setCarVin(carVinComboBox.getValue());
            insurance.setCustomerId(customerComboBox.getValue().getId());
            insurance.setInsuranceType(insuranceTypeComboBox.getValue());
            insurance.setInsuranceNumber(insuranceNumberField.getText());
            insurance.setPrice(Double.parseDouble(priceField.getText()));
            insurance.setStartDate(startDatePicker.getValue());
            insurance.setEndDate(endDatePicker.getValue());

            okClicked = true;
            closeDialog();
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (carVinComboBox.getValue() == null) {
            errorMessage += "Выберите автомобиль!\n";
        }
        if (customerComboBox.getValue() == null) {
            errorMessage += "Выберите клиента!\n";
        }
        if (insuranceTypeComboBox.getValue() == null) {
            errorMessage += "Выберите тип страховки!\n";
        }
        if (insuranceNumberField.getText() == null || insuranceNumberField.getText().length() == 0) {
            errorMessage += "Введите номер страховки!\n";
        }
        if (priceField.getText() == null || priceField.getText().length() == 0) {
            errorMessage += "Введите стоимость!\n";
        }
        if (startDatePicker.getValue() == null) {
            errorMessage += "Выберите дату начала!\n";
        }
        if (endDatePicker.getValue() == null) {
            errorMessage += "Выберите дату окончания!\n";
        }
        if (startDatePicker.getValue() != null && endDatePicker.getValue() != null &&
            startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            errorMessage += "Дата начала не может быть позже даты окончания!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Пожалуйста, исправьте следующие поля:");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }
} 