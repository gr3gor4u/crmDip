package com.example.javacrm.controller;

import com.example.javacrm.model.User;
import com.example.javacrm.service.DatabaseService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.util.regex.Pattern;

public class RegisterController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private DatabaseService databaseService;
    private Stage stage;
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    public void setDatabaseService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String email = emailField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();

        // Проверка заполнения всех полей
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || 
            email.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            showError("Пожалуйста, заполните все поля.");
            return;
        }

        // Проверка email
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showError("Пожалуйста, введите корректный email адрес.");
            return;
        }

        // Проверка пароля
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            showError("Пароль должен содержать минимум 8 символов, включая цифры, " +
                     "строчные и заглавные буквы, и специальные символы (@#$%^&+=).");
            return;
        }

        // Проверка совпадения паролей
        if (!password.equals(confirmPassword)) {
            showError("Пароли не совпадают.");
            return;
        }

        try {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            user.setRole("MANAGER"); // Устанавливаем роль MANAGER для новых пользователей
            user.setFirstName(firstName);
            user.setLastName(lastName);
            
            databaseService.registerUser(user);
            showInfo("Регистрация успешна! Теперь войдите в систему.");
            loadLoginScreen();
        } catch (Exception e) {
            showError("Ошибка регистрации: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        loadLoginScreen();
    }

    private void loadLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loader.load();
            LoginController loginController = loader.getController();
            loginController.setDatabaseService(databaseService);
            loginController.setStage(stage);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Вход в систему");
        } catch (Exception e) {
            showError("Ошибка загрузки экрана входа: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorLabel.setVisible(true);
    }

    private void showInfo(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: green;");
        errorLabel.setVisible(true);
    }
} 