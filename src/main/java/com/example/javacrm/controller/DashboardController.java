package com.example.javacrm.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import com.example.javacrm.service.CustomerService;
import com.example.javacrm.service.CarService;
import com.example.javacrm.service.DatabaseService;
import com.example.javacrm.model.User;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert;
import java.io.IOException;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.GridPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import javafx.scene.Scene;
import com.example.javacrm.service.UserService;

public class DashboardController {
    @FXML private Label totalCustomersLabel;
    @FXML private Label totalCarsLabel;
    @FXML private Label availableCarsLabel;
    @FXML private Label welcomeLabel;
    @FXML private VBox contentArea;
    // Можно добавить другие лейблы для статистики

    private CustomerService customerService;
    private CarService carService;
    private User currentUser;
    private Stage stage;

    @FXML
    public void initialize() {
        try {
            customerService = new CustomerService(DatabaseService.getInstance());
            carService = new CarService(DatabaseService.getInstance());
            updateStatistics();
            updateWelcomeLabel();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Ошибка инициализации", "Не удалось инициализировать контроллер: " + e.getMessage());
        }
    }

    private void updateStatistics() {
        try {
            int totalCustomers = customerService.getAllCustomers().size();
            int totalCars = carService.getAllCars().size();
            int availableCars = (int) carService.getAllCars().stream()
                .filter(car -> "AVAILABLE".equals(car.getStatus()))
                .count();

            totalCustomersLabel.setText(String.valueOf(totalCustomers));
            totalCarsLabel.setText(String.valueOf(totalCars));
            availableCarsLabel.setText(String.valueOf(availableCars));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Ошибка обновления статистики", "Не удалось обновить статистику: " + e.getMessage());
        }
    }

    public void setUser(User user) {
        this.currentUser = user;
        updateWelcomeLabel();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void updateWelcomeLabel() {
        if (currentUser != null) {
            welcomeLabel.setText("Добро пожаловать, " + currentUser.getFirstName() + " " + currentUser.getLastName());
        }
    }

    @FXML
    private void refreshDashboard() {
        updateStatistics();
    }

    @FXML
    private void showDashboard() {
        contentArea.getChildren().clear();
        contentArea.getChildren().addAll(
            createHeaderBox(),
            createStatisticsGrid(),
            createRecentDealsSection(),
            createSalesChartSection()
        );
        updateStatistics();
    }

    private HBox createHeaderBox() {
        HBox headerBox = new HBox();
        headerBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        headerBox.setSpacing(10);
        
        Label dashboardTitle = new Label("Дашборд");
        dashboardTitle.getStyleClass().add("section-title");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        
        Button refreshButton = new Button("Обновить");
        refreshButton.setOnAction(e -> refreshDashboard());
        
        headerBox.getChildren().addAll(dashboardTitle, spacer, refreshButton);
        return headerBox;
    }

    private GridPane createStatisticsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        
        // Статистика клиентов
        VBox customersCard = new VBox();
        customersCard.getStyleClass().add("card");
        Label customersTitle = new Label("Всего клиентов");
        customersTitle.getStyleClass().add("card-title");
        Label customersValue = new Label(String.valueOf(customerService.getAllCustomers().size()));
        customersValue.getStyleClass().add("card-value");
        customersCard.getChildren().addAll(customersTitle, customersValue);
        grid.add(customersCard, 0, 0);
        
        // Статистика сделок
        VBox dealsCard = new VBox();
        dealsCard.getStyleClass().add("card");
        Label dealsTitle = new Label("Всего сделок");
        dealsTitle.getStyleClass().add("card-title");
        Label dealsValue = new Label("0"); // TODO: Добавить подсчет сделок
        dealsValue.getStyleClass().add("card-value");
        dealsCard.getChildren().addAll(dealsTitle, dealsValue);
        grid.add(dealsCard, 1, 0);
        
        // Статистика автомобилей
        VBox carsCard = new VBox();
        carsCard.getStyleClass().add("card");
        Label carsTitle = new Label("Всего автомобилей");
        carsTitle.getStyleClass().add("card-title");
        Label carsValue = new Label(String.valueOf(carService.getAllCars().size()));
        carsValue.getStyleClass().add("card-value");
        carsCard.getChildren().addAll(carsTitle, carsValue);
        grid.add(carsCard, 2, 0);
        
        return grid;
    }

    private VBox createRecentDealsSection() {
        VBox section = new VBox(10);
        Label title = new Label("Последние сделки");
        title.getStyleClass().add("section-title");
        
        TableView<Object> table = new TableView<>();
        table.getColumns().addAll(
            createTableColumn("ID", "id", 50),
            createTableColumn("Клиент", "customer", 200),
            createTableColumn("Автомобиль", "car", 200),
            createTableColumn("Сумма", "amount", 100),
            createTableColumn("Дата", "date", 150)
        );
        
        section.getChildren().addAll(title, table);
        return section;
    }

    private VBox createSalesChartSection() {
        VBox section = new VBox(10);
        Label title = new Label("График продаж");
        title.getStyleClass().add("section-title");
        
        LineChart<String, Number> chart = new LineChart<>(
            new CategoryAxis(),
            new NumberAxis()
        );
        chart.setTitle("Продажи по месяцам");
        
        section.getChildren().addAll(title, chart);
        return section;
    }

    private <T> TableColumn<T, ?> createTableColumn(String title, String property, double width) {
        TableColumn<T, ?> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    @FXML
    private void handleCars() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/cars.fxml"));
            Parent carsContent = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(carsContent);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Ошибка", "Не удалось загрузить раздел автомобилей: " + e.getMessage());
        }
    }

    @FXML
    private void handleCustomers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/customers.fxml"));
            Parent customersContent = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(customersContent);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Ошибка", "Не удалось загрузить раздел клиентов: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeals() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/deals.fxml"));
            Parent dealsContent = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(dealsContent);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Ошибка", "Не удалось загрузить раздел сделок: " + e.getMessage());
        }
    }

    @FXML
    private void handleInsurance() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/insurance.fxml"));
            Parent insuranceContent = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(insuranceContent);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Ошибка", "Не удалось загрузить раздел страховки: " + e.getMessage());
        }
    }

    @FXML
    private void handleEquipment() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/equipment.fxml"));
            Parent equipmentContent = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(equipmentContent);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Ошибка", "Не удалось загрузить раздел дополнительного оборудования: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent loginContent = loader.load();

            LoginController loginController = loader.getController();
            loginController.setUserService(new UserService(DatabaseService.getInstance())); // или ваш ServiceInitializer
            loginController.setStage(stage);

            Scene scene = new Scene(loginContent);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showError("Ошибка", "Не удалось выполнить выход: " + e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 