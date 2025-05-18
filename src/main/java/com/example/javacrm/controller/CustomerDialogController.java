package com.example.javacrm.controller;

import com.example.javacrm.model.Customer;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class CustomerDialogController {
    @FXML private TextField lastNameField;
    @FXML private TextField firstNameField;
    @FXML private TextField middleNameField;
    @FXML private ComboBox<String> maritalStatusComboBox;
    @FXML private TextField childrenCountField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField passportField;

    private Customer customer;

    @FXML
    public void initialize() {
        maritalStatusComboBox.getItems().addAll(
            "Холост/Не замужем",
            "Женат/Замужем",
            "Разведен/Разведена",
            "Вдовец/Вдова"
        );
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        if (customer != null) {
            lastNameField.setText(customer.getLastName());
            firstNameField.setText(customer.getFirstName());
            middleNameField.setText(customer.getMiddleName());
            maritalStatusComboBox.setValue(customer.getMaritalStatus());
            childrenCountField.setText(String.valueOf(customer.getChildrenCount()));
            emailField.setText(customer.getEmail());
            phoneField.setText(customer.getPhone());
            passportField.setText(customer.getPassportNumber());
        }
    }

    public Customer getCustomer() {
        if (customer == null) {
            customer = new Customer();
        }
        
        customer.setLastName(lastNameField.getText());
        customer.setFirstName(firstNameField.getText());
        customer.setMiddleName(middleNameField.getText());
        customer.setMaritalStatus(maritalStatusComboBox.getValue());
        customer.setChildrenCount(Integer.parseInt(childrenCountField.getText()));
        customer.setEmail(emailField.getText());
        customer.setPhone(phoneField.getText());
        customer.setPassportNumber(passportField.getText());
        
        return customer;
    }
} 