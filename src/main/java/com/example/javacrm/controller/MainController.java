package com.example.javacrm.controller;

import com.example.javacrm.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    private Label userLabel;
    
    @FXML
    private StackPane contentArea;
    
    @FXML
    private Button dashboardButton;
    
    @FXML
    private Button carsButton;
    
    @FXML
    private Button customersButton;
    
    @FXML
    private Button dealsButton;
    
    @FXML
    private Button reportsButton;
    
    private User currentUser;
    private Stage stage;
    
    @FXML
    public void initialize() {
        // Устанавливаем дашборд как активную вкладку при запуске
        showDashboard();
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateUserLabel();
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    private void updateUserLabel() {
        if (currentUser != null) {
            userLabel.setText("Пользователь: " + currentUser.getFullName());
        }
    }
    
    private void setActiveButton(Button activeButton) {
        // Сбрасываем стили всех кнопок
        dashboardButton.getStyleClass().remove("active");
        carsButton.getStyleClass().remove("active");
        customersButton.getStyleClass().remove("active");
        dealsButton.getStyleClass().remove("active");
        reportsButton.getStyleClass().remove("active");
        
        // Устанавливаем стиль для активной кнопки
        activeButton.getStyleClass().add("active");
    }
    
    @FXML
    private void showDashboard() {
        setActiveButton(dashboardButton);
        loadContent("/fxml/dashboard.fxml");
    }
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.setStage(stage);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Вход в систему");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleCars() {
        setActiveButton(carsButton);
        loadContent("/fxml/cars.fxml");
    }
    
    @FXML
    private void handleCustomers() {
        setActiveButton(customersButton);
        loadContent("/fxml/customers.fxml");
    }
    
    @FXML
    private void handleDeals() {
        setActiveButton(dealsButton);
        loadContent("/fxml/deals.fxml");
    }
    
    @FXML
    private void handleReports() {
        setActiveButton(reportsButton);
        loadContent("/fxml/reports.fxml");
    }
    
    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 