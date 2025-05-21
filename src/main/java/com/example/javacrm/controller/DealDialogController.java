package com.example.javacrm.controller;

import com.example.javacrm.model.Deal;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.math.BigDecimal;

public class DealDialogController {
    @FXML
    private TextField customerField;
    @FXML
    private TextField carField;
    @FXML
    private TextField amountField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TextField notesField;
    @FXML
    private DialogPane dialogPane;

    private Deal deal;

    @FXML
    public void initialize() {
        ObservableList<String> statuses = FXCollections.observableArrayList(
            "Новая",
            "В процессе",
            "Завершена",
            "Отменена"
        );
        statusComboBox.setItems(statuses);
    }

    public void setDeal(Deal deal) {
        this.deal = deal;
        if (deal != null) {
            if (deal.getCustomer() != null) {
                customerField.setText(deal.getCustomer().getFullName());
            }
            if (deal.getCar() != null) {
                carField.setText(deal.getCar().getBrand() + " " + deal.getCar().getModel());
            }
            if (deal.getTotalPrice() != null) {
                amountField.setText(deal.getTotalPrice().toString());
            }
            statusComboBox.setValue(deal.getStatus());
            if (deal.getNotes() != null) {
                notesField.setText(deal.getNotes());
            }
        }
    }

    public Deal getDeal() {
        if (deal == null) {
            deal = new Deal();
        }
        deal.setTotalPrice(new BigDecimal(amountField.getText()));
        deal.setStatus(statusComboBox.getValue());
        deal.setNotes(notesField.getText());
        return deal;
    }

    @FXML
    private void handleOK() {
        if (isInputValid()) {
            dialogPane.getButtonTypes().add(ButtonType.OK);
        }
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (amountField.getText() == null || amountField.getText().length() == 0) {
            errorMessage += "Сумма не может быть пустой!\n";
        } else {
            try {
                new BigDecimal(amountField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "Сумма должна быть числом!\n";
            }
        }

        if (statusComboBox.getValue() == null) {
            errorMessage += "Выберите статус!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Показываем сообщение об ошибке
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Ошибка ввода");
            alert.setHeaderText("Пожалуйста, исправьте следующие поля:");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;
        }
    }
} 