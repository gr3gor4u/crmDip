package com.example.javacrm.controller;

import com.example.javacrm.service.DealershipService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class MainController {
    @FXML
    private StackPane contentArea;

    @FXML
    private Label statusLabel;

    private final DealershipService dealershipService;
    private final ApplicationContext applicationContext;

    @Autowired
    public MainController(DealershipService dealershipService, ApplicationContext applicationContext) {
        this.dealershipService = dealershipService;
        this.applicationContext = applicationContext;
    }

    @FXML
    public void initialize() {
        showDashboard();
    }

    @FXML
    private void showDashboard() {
        loadView("/fxml/dashboard.fxml");
        updateStatus("Дашборд загружен");
    }

    @FXML
    private void showCustomers() {
        loadView("/fxml/customers.fxml");
        updateStatus("Список клиентов загружен");
    }

    @FXML
    private void showCars() {
        loadView("/fxml/cars.fxml");
        updateStatus("Каталог автомобилей загружен");
    }

    @FXML
    private void showDeals() {
        loadView("/fxml/deals.fxml");
        updateStatus("Список сделок загружен");
    }

    @FXML
    private void showService() {
        loadView("/fxml/service.fxml");
        updateStatus("Сервисный модуль загружен");
    }

    @FXML
    private void showReports() {
        loadView("/fxml/reports.fxml");
        updateStatus("Отчеты загружены");
    }

    @FXML
    private void handleLogout() {
        // TODO: Implement logout logic
        updateStatus("Выход из системы...");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(applicationContext::getBean);
            Parent view = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            updateStatus("Ошибка загрузки представления: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
    }
} 