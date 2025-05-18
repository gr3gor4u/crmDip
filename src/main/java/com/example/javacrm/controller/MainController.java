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
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;

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
    private Button insuranceButton;
    
    @FXML
    private Button equipmentButton;
    
    @FXML
    private Button logoutButton;
    
    private User currentUser;
    private Stage stage;
    
    @FXML
    public void initialize() {
        // Устанавливаем обработчики событий для кнопок
        dashboardButton.setOnAction(e -> showDashboard());
        carsButton.setOnAction(e -> showCars());
        customersButton.setOnAction(e -> showCustomers());
        dealsButton.setOnAction(e -> showDeals());
        insuranceButton.setOnAction(e -> showInsurance());
        equipmentButton.setOnAction(e -> showEquipment());
        logoutButton.setOnAction(e -> handleLogout());
        
        // Показываем дашборд по умолчанию
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
    
    @FXML
    private void showDashboard() {
        loadContent("/fxml/dashboard.fxml");
    }
    
    @FXML
    private void showCars() {
        loadContent("/fxml/cars.fxml");
    }
    
    @FXML
    private void showCustomers() {
        loadContent("/fxml/customers.fxml");
    }
    
    @FXML
    private void showDeals() {
        loadContent("/fxml/deals.fxml");
    }
    
    @FXML
    private void showInsurance() {
        loadContent("/fxml/insurance.fxml");
    }
    
    @FXML
    private void showEquipment() {
        loadContent("/fxml/equipment.fxml");
    }
    
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Ошибка при выходе из системы: " + e.getMessage());
        }
    }
    
    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Ошибка при загрузке содержимого: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 