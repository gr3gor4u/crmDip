package com.example.javacrm.controller;

import com.example.javacrm.model.Car;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class CarDialogController {
    @FXML private TextField modelField;
    @FXML private TextField brandField;
    @FXML private TextField yearField;
    @FXML private TextField vinField;
    @FXML private TextField colorField;
    @FXML private TextField kuzovField;
    @FXML private TextField obemDvigField;
    @FXML private TextField horsePowerField;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> statusComboBox;

    private Car car;

    @FXML
    public void initialize() {
        statusComboBox.getItems().addAll(
            "В наличии",
            "Забронирован",
            "Продан",
            "На ремонте"
        );
    }

    public void setCar(Car car) {
        this.car = car;
        if (car != null) {
            modelField.setText(car.getModel());
            brandField.setText(car.getBrand());
            yearField.setText(String.valueOf(car.getYear()));
            vinField.setText(car.getVin());
            colorField.setText(car.getColor());
            kuzovField.setText(car.getKuzov());
            obemDvigField.setText(String.valueOf(car.getObemDvig()));
            horsePowerField.setText(String.valueOf(car.getHorsePower()));
            priceField.setText(String.valueOf(car.getPrice()));
            statusComboBox.setValue(car.getStatus());
        }
    }

    public Car getCar() {
        if (car == null) {
            car = new Car();
        }
        
        car.setModel(modelField.getText());
        car.setBrand(brandField.getText());
        car.setYear(Integer.parseInt(yearField.getText()));
        car.setVin(vinField.getText());
        car.setColor(colorField.getText());
        car.setKuzov(kuzovField.getText());
        car.setObemDvig(Double.parseDouble(obemDvigField.getText()));
        car.setHorsePower(Integer.parseInt(horsePowerField.getText()));
        car.setPrice(Double.parseDouble(priceField.getText()));
        car.setStatus(statusComboBox.getValue());
        
        return car;
    }
} 