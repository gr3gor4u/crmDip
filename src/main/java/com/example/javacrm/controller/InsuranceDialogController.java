package com.example.javacrm.controller;

import com.example.javacrm.model.Insurance;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDate;

public class InsuranceDialogController {
    @FXML
    private TextField customerIdField;
    @FXML
    private TextField carVinField;
    @FXML
    private ComboBox<Insurance.InsuranceType> insuranceTypeComboBox;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker expiryDatePicker;
    @FXML
    private ComboBox<String> statusComboBox;

    private Stage dialogStage;
    private Insurance insurance;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
        insuranceTypeComboBox.getItems().addAll(Insurance.InsuranceType.values());
        insuranceTypeComboBox.setValue(Insurance.InsuranceType.ОСАГО);
        
        statusComboBox.getItems().addAll("Активна", "Истекла");
        statusComboBox.setValue("Активна");
        
        startDatePicker.setValue(LocalDate.now());
        expiryDatePicker.setValue(LocalDate.now().plusYears(1));
        
        startDatePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                expiryDatePicker.setValue(newVal.plusYears(1));
            }
        });
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setInsurance(Insurance insurance) {
        this.insurance = insurance;
        
        customerIdField.setText(String.valueOf(insurance.getCustomerId()));
        carVinField.setText(insurance.getCarVin());
        insuranceTypeComboBox.setValue(insurance.getInsuranceType());
        startDatePicker.setValue(insurance.getStartDate());
        expiryDatePicker.setValue(insurance.getExpiryDate());
        statusComboBox.setValue(insurance.getStatus());
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOK() {
        if (isInputValid()) {
            insurance.setCustomerId(Long.parseLong(customerIdField.getText()));
            insurance.setCarVin(carVinField.getText());
            insurance.setInsuranceType(insuranceTypeComboBox.getValue());
            insurance.setStartDate(startDatePicker.getValue());
            insurance.setExpiryDate(expiryDatePicker.getValue());
            insurance.setStatus(statusComboBox.getValue());
            
            okClicked = true;
            dialogStage.close();
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (customerIdField.getText() == null || customerIdField.getText().isEmpty()) {
            errorMessage += "ID клиента не может быть пустым!\n";
        } else {
            try {
                Long.parseLong(customerIdField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "ID клиента должен быть числом!\n";
            }
        }

        if (carVinField.getText() == null || carVinField.getText().isEmpty()) {
            errorMessage += "VIN автомобиля не может быть пустым!\n";
        }

        if (insuranceTypeComboBox.getValue() == null) {
            errorMessage += "Тип страховки не может быть пустым!\n";
        }

        if (startDatePicker.getValue() == null) {
            errorMessage += "Дата начала не может быть пустой!\n";
        }

        if (expiryDatePicker.getValue() == null) {
            errorMessage += "Дата окончания не может быть пустой!\n";
        }

        if (startDatePicker.getValue() != null && expiryDatePicker.getValue() != null &&
            startDatePicker.getValue().isAfter(expiryDatePicker.getValue())) {
            errorMessage += "Дата начала не может быть позже даты окончания!\n";
        }

        if (statusComboBox.getValue() == null) {
            errorMessage += "Статус не может быть пустым!\n";
        }

        if (errorMessage.isEmpty()) {
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
} 