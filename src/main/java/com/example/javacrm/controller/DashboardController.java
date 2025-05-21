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
import com.example.javacrm.service.DealService;
import com.example.javacrm.model.Deal;
import com.example.javacrm.model.Car;
import com.example.javacrm.model.Customer;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;

public class DashboardController {
    @FXML private Label totalCustomersLabel;
    @FXML private Label totalCarsLabel;
    @FXML private Label availableCarsLabel;
    @FXML private Label welcomeLabel;
    @FXML private VBox contentArea;
    // Можно добавить другие лейблы для статистики

    private CustomerService customerService;
    private CarService carService;
    private DealService dealService;
    private User currentUser;
    private Stage stage;
    private ProgressIndicator refreshIndicator;

    @FXML
    public void initialize() {
        try {
            customerService = new CustomerService(DatabaseService.getInstance());
            carService = new CarService(DatabaseService.getInstance());
            dealService = new DealService(DatabaseService.getInstance(), customerService, carService, null, null);
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
            int totalDeals = dealService.getAllDeals().size();

            totalCustomersLabel.setText(String.valueOf(totalCustomers));
            totalCarsLabel.setText(String.valueOf(totalCars));
            availableCarsLabel.setText(String.valueOf(availableCars));
            // Если есть отдельный лейбл для сделок, можно добавить:
            // totalDealsLabel.setText(String.valueOf(totalDeals));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Ошибка обновления статистики", "Не удалось обновить статистику: " + e.getMessage());
        }
    }

    public void setUser(User user) {
        this.currentUser = user;
        updateWelcomeLabel();
        showDashboard();
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
        refreshIndicator = new ProgressIndicator();
        refreshIndicator.setPrefSize(24, 24);
        refreshIndicator.setVisible(false);
        refreshButton.setOnAction(e -> startRefreshAnimation());
        
        headerBox.getChildren().addAll(dashboardTitle, spacer, refreshButton, refreshIndicator);
        return headerBox;
    }

    private void startRefreshAnimation() {
        refreshIndicator.setVisible(true);
        Task<Void> refreshTask = new Task<>() {
            @Override
            protected Void call() {
                // Имитация задержки для анимации (можно убрать или уменьшить)
                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
                Platform.runLater(() -> {
                    updateStatistics();
                    showDashboard();
                    refreshIndicator.setVisible(false);
                });
                return null;
            }
        };
        new Thread(refreshTask).start();
    }

    private GridPane createStatisticsGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        
        VBox customersCard = new VBox();
        customersCard.getStyleClass().add("card");
        Label customersTitle = new Label("Всего клиентов");
        customersTitle.getStyleClass().add("card-title");
        Label customersValue = new Label(String.valueOf(customerService.getAllCustomers().size()));
        customersValue.getStyleClass().add("card-value");
        customersCard.getChildren().addAll(customersTitle, customersValue);
        grid.add(customersCard, 0, 0);
        
        VBox dealsCard = new VBox();
        dealsCard.getStyleClass().add("card");
        Label dealsTitle = new Label("Всего сделок");
        dealsTitle.getStyleClass().add("card-title");
        int totalDeals = dealService.getAllDeals().size();
        Label dealsValue = new Label(String.valueOf(totalDeals));
        dealsValue.getStyleClass().add("card-value");
        dealsCard.getChildren().addAll(dealsTitle, dealsValue);
        grid.add(dealsCard, 1, 0);
        
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
        
        TableView<Deal> table = new TableView<>();
        TableColumn<Deal, Long> idCol = new TableColumn<>("ID");
        idCol.setPrefWidth(50);
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Deal, String> customerCol = new TableColumn<>("Клиент");
        customerCol.setPrefWidth(200);
        customerCol.setCellValueFactory(cellData -> {
            Deal deal = cellData.getValue();
            Customer c = deal.getCustomer();
            return new javafx.beans.property.SimpleStringProperty(
                c != null ? c.getLastName() + " " + c.getFirstName() : "");
        });
        TableColumn<Deal, String> carCol = new TableColumn<>("Автомобиль");
        carCol.setPrefWidth(200);
        carCol.setCellValueFactory(cellData -> {
            Deal deal = cellData.getValue();
            Car car = deal.getCar();
            return new javafx.beans.property.SimpleStringProperty(
                car != null ? car.getBrand() + " " + car.getModel() : "");
        });
        TableColumn<Deal, String> amountCol = new TableColumn<>("Сумма");
        amountCol.setPrefWidth(100);
        amountCol.setCellValueFactory(cellData -> {
            Deal deal = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                deal.getTotalPrice() != null ? deal.getTotalPrice().toString() : "");
        });
        TableColumn<Deal, String> dateCol = new TableColumn<>("Дата");
        dateCol.setPrefWidth(150);
        dateCol.setCellValueFactory(cellData -> {
            Deal deal = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                deal.getDealDate() != null ? deal.getDealDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) : "");
        });
        table.getColumns().addAll(idCol, customerCol, carCol, amountCol, dateCol);
        // Показываем только 10 последних сделок
        List<Deal> lastDeals = dealService.getAllDeals().stream()
            .sorted(Comparator.comparing(Deal::getDealDate, Comparator.nullsLast(Comparator.reverseOrder())))
            .limit(10)
            .collect(Collectors.toList());
        table.setItems(FXCollections.observableArrayList(lastDeals));
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
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Сделки");
        // Группируем сделки по месяцам
        Map<String, Long> salesByMonth = dealService.getAllDeals().stream()
            .filter(d -> d.getDealDate() != null)
            .collect(Collectors.groupingBy(
                d -> d.getDealDate().format(DateTimeFormatter.ofPattern("MM.yyyy")),
                Collectors.counting()
            ));
        salesByMonth.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));
        chart.getData().add(series);
        section.getChildren().addAll(title, chart);
        return section;
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