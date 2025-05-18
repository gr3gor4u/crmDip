package com.example.javacrm.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import com.example.javacrm.model.Deal;
import com.example.javacrm.service.DealService;
import com.example.javacrm.service.CustomerService;
import com.example.javacrm.service.CarService;
import com.example.javacrm.service.DatabaseService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController {
    @FXML
    private Label totalCustomersLabel;
    
    @FXML
    private Label newCustomersLabel;
    
    @FXML
    private Label totalDealsLabel;
    
    @FXML
    private Label activeDealsLabel;
    
    @FXML
    private Label totalCarsLabel;
    
    @FXML
    private Label availableCarsLabel;
    
    @FXML
    private TableView<Deal> recentDealsTable;
    
    @FXML
    private TableColumn<Deal, Long> dealIdColumn;
    
    @FXML
    private TableColumn<Deal, String> customerColumn;
    
    @FXML
    private TableColumn<Deal, String> carColumn;
    
    @FXML
    private TableColumn<Deal, Double> amountColumn;
    
    @FXML
    private TableColumn<Deal, LocalDate> dateColumn;
    
    @FXML
    private LineChart<String, Number> salesChart;
    
    private final DealService dealService;
    private final CustomerService customerService;
    private final CarService carService;
    
    public DashboardController() {
        this.dealService = new DealService();
        this.customerService = new CustomerService();
        this.carService = new CarService(DatabaseService.getInstance());
    }
    
    @FXML
    public void initialize() {
        setupTableColumns();
        refreshDashboard();
    }
    
    private void setupTableColumns() {
        dealIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        carColumn.setCellValueFactory(new PropertyValueFactory<>("carModel"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        dateColumn.setCellFactory(column -> new TableCell<Deal, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
                }
            }
        });
    }
    
    @FXML
    public void refreshDashboard() {
        // Обновляем статистику
        updateStatistics();
        
        // Обновляем таблицу последних сделок
        updateRecentDeals();
        
        // Обновляем график продаж
        updateSalesChart();
    }
    
    private void updateStatistics() {
        // Обновляем статистику клиентов
        long totalCustomers = customerService.getTotalCustomers();
        long newCustomers = customerService.getNewCustomersToday();
        totalCustomersLabel.setText(String.valueOf(totalCustomers));
        newCustomersLabel.setText("Новых сегодня: " + newCustomers);
        
        // Обновляем статистику сделок
        long totalDeals = dealService.getTotalDeals();
        long activeDeals = dealService.getActiveDeals();
        totalDealsLabel.setText(String.valueOf(totalDeals));
        activeDealsLabel.setText("Активных: " + activeDeals);
        
        // Обновляем статистику автомобилей
        long totalCars = carService.getAllCars().size();
        long availableCars = carService.getAllCars().stream()
            .filter(car -> "AVAILABLE".equals(car.getStatus()))
            .count();
        totalCarsLabel.setText(String.valueOf(totalCars));
        availableCarsLabel.setText("Доступно: " + availableCars);
    }
    
    private void updateRecentDeals() {
        List<Deal> recentDeals = dealService.getRecentDeals(10);
        recentDealsTable.getItems().setAll(recentDeals);
    }
    
    private void updateSalesChart() {
        salesChart.getData().clear();
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Продажи");
        
        // Получаем данные о продажах за последние 6 месяцев
        List<Deal> monthlyDeals = dealService.getMonthlyDeals(6);
        
        for (Deal deal : monthlyDeals) {
            series.getData().add(new XYChart.Data<>(
                deal.getDate().format(DateTimeFormatter.ofPattern("MMM yyyy")),
                deal.getAmount()
            ));
        }
        
        salesChart.getData().add(series);
    }
} 