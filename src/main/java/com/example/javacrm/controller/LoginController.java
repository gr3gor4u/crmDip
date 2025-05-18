package com.example.javacrm.controller;

import com.example.javacrm.model.User;
import com.example.javacrm.service.DatabaseService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    private DatabaseService databaseService;
    private Stage stage;
    
    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Ошибка входа", "Пожалуйста, введите имя пользователя и пароль");
            return;
        }
        
        databaseService.authenticateUser(username, password).ifPresentOrElse(
            this::loadMainScreen,
            () -> showError("Ошибка входа", "Неверное имя пользователя или пароль")
        );
    }
    
    private void loadMainScreen(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            
            MainController controller = loader.getController();
            controller.setCurrentUser(user);
            controller.setStage(stage);
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            stage.setScene(scene);
            stage.setTitle("CarStore CRM - Главное меню");
            stage.show();
        } catch (Exception e) {
            showError("Ошибка", "Не удалось загрузить главный экран: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Parent root = loader.load();
            RegisterController registerController = loader.getController();
            registerController.setDatabaseService(databaseService);
            registerController.setStage(stage);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Регистрация");
        } catch (Exception e) {
            showError("Ошибка", "Не удалось загрузить окно регистрации: " + e.getMessage());
        }
    }
    
    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 