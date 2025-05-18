package com.example.javacrm.controller;

import com.example.javacrm.model.User;
import com.example.javacrm.service.UserService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;

    private UserService userService;
    private Stage stage;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Пожалуйста, введите имя пользователя и пароль");
            return;
        }

        try {
            User user = userService.authenticate(username, password);
            if (user != null) {
                loadMainView(user);
            } else {
                showError("Неверное имя пользователя или пароль");
            }
        } catch (Exception e) {
            showError("Ошибка входа: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
            Parent root = loader.load();
            
            RegisterController controller = loader.getController();
            controller.setUserService(userService);
            controller.setStage(stage);
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Не удалось загрузить форму регистрации: " + e.getMessage());
        }
    }

    private void loadMainView(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            
            DashboardController controller = loader.getController();
            controller.setUser(user);
            controller.setStage(stage);
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Не удалось загрузить главное окно: " + e.getMessage());
        }
    }

    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 