package com.example.javacrm.controller;

import com.example.javacrm.service.DealershipService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Controller
public class ReportsController {
    @FXML private VBox reportsContainer;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> reportTypeComboBox;
    
    @FXML private PieChart salesByBrandChart;
    @FXML private BarChart<String, Number> salesByMonthChart;
    @FXML private LineChart<String, Number> revenueChart;
    @FXML private TableView<Map.Entry<String, BigDecimal>> topCustomersTable;
    @FXML private TableView<Map.Entry<String, BigDecimal>> topCarsTable;
    
    @FXML private TableColumn<Map.Entry<String, BigDecimal>, String> customerNameColumn;
    @FXML private TableColumn<Map.Entry<String, BigDecimal>, BigDecimal> customerAmountColumn;
    @FXML private TableColumn<Map.Entry<String, BigDecimal>, String> carNameColumn;
    @FXML private TableColumn<Map.Entry<String, BigDecimal>, BigDecimal> carAmountColumn;

    private final DealershipService dealershipService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    @Autowired
    public ReportsController(DealershipService dealershipService) {
        this.dealershipService = dealershipService;
    }

    @FXML
    public void initialize() {
        setupReportTypeComboBox();
        setupDatePickers();
        setupTableColumns();
        loadReports();
    }

    private void setupTableColumns() {
        // Настройка колонок для таблицы топ клиентов
        customerNameColumn.setCellValueFactory(param -> 
            new SimpleStringProperty(param.getValue().getKey()));
        customerAmountColumn.setCellValueFactory(param -> 
            new SimpleObjectProperty<>(param.getValue().getValue()));
        customerAmountColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(formatCurrency(amount));
                }
            }
        });

        // Настройка колонок для таблицы топ автомобилей
        carNameColumn.setCellValueFactory(param -> 
            new SimpleStringProperty(param.getValue().getKey()));
        carAmountColumn.setCellValueFactory(param -> 
            new SimpleObjectProperty<>(param.getValue().getValue()));
        carAmountColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                } else {
                    setText(formatCurrency(amount));
                }
            }
        });
    }

    private void setupReportTypeComboBox() {
        reportTypeComboBox.setItems(FXCollections.observableArrayList(
            "Общая статистика",
            "Продажи по брендам",
            "Продажи по месяцам",
            "Выручка",
            "Топ клиентов",
            "Топ автомобилей"
        ));
        
        reportTypeComboBox.setOnAction(event -> loadReports());
    }

    private void setupDatePickers() {
        // Устанавливаем начальную дату на начало текущего месяца
        startDatePicker.setValue(LocalDateTime.now().withDayOfMonth(1).toLocalDate());
        // Устанавливаем конечную дату на текущую дату
        endDatePicker.setValue(LocalDateTime.now().toLocalDate());
        
        startDatePicker.setOnAction(event -> loadReports());
        endDatePicker.setOnAction(event -> loadReports());
    }

    @FXML
    private void loadReports() {
        LocalDateTime startDate = startDatePicker.getValue().atStartOfDay();
        LocalDateTime endDate = endDatePicker.getValue().atTime(23, 59, 59);
        String reportType = reportTypeComboBox.getValue();

        if (reportType == null) {
            return;
        }

        switch (reportType) {
            case "Общая статистика" -> showGeneralStatistics(startDate, endDate);
            case "Продажи по брендам" -> showSalesByBrand(startDate, endDate);
            case "Продажи по месяцам" -> showSalesByMonth(startDate, endDate);
            case "Выручка" -> showRevenue(startDate, endDate);
            case "Топ клиентов" -> showTopCustomers(startDate, endDate);
            case "Топ автомобилей" -> showTopCars(startDate, endDate);
        }
    }

    private void showGeneralStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        // Очищаем контейнер
        reportsContainer.getChildren().clear();

        // Создаем метки для отображения статистики
        Label totalSalesLabel = new Label(String.format("Общая выручка: %s", 
            formatCurrency(dealershipService.getTotalSales(startDate, endDate))));
        Label totalDealsLabel = new Label(String.format("Количество сделок: %d", 
            dealershipService.getTotalDeals(startDate, endDate)));
        Label avgDealLabel = new Label(String.format("Средний чек: %s", 
            formatCurrency(dealershipService.getAverageDealAmount(startDate, endDate))));

        // Добавляем метки в контейнер
        reportsContainer.getChildren().addAll(totalSalesLabel, totalDealsLabel, avgDealLabel);
    }

    private void showSalesByBrand(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Long> salesByBrand = dealershipService.getSalesByBrand(startDate, endDate);
        
        salesByBrandChart.getData().clear();
        salesByBrand.forEach((brand, count) -> 
            salesByBrandChart.getData().add(new PieChart.Data(brand, count))
        );
    }

    private void showSalesByMonth(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Long> salesByMonth = dealershipService.getSalesByMonth(startDate, endDate);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Продажи по месяцам");
        
        salesByMonth.forEach((month, count) -> 
            series.getData().add(new XYChart.Data<>(month, count))
        );

        salesByMonthChart.getData().clear();
        salesByMonthChart.getData().add(series);
    }

    private void showRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, BigDecimal> revenueByMonth = dealershipService.getRevenueByMonth(startDate, endDate);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Выручка по месяцам");
        
        revenueByMonth.forEach((month, amount) -> 
            series.getData().add(new XYChart.Data<>(month, amount))
        );

        revenueChart.getData().clear();
        revenueChart.getData().add(series);
    }

    private void showTopCustomers(LocalDateTime startDate, LocalDateTime endDate) {
        List<Map.Entry<String, BigDecimal>> topCustomers = 
            dealershipService.getTopCustomers(startDate, endDate);
        
        topCustomersTable.setItems(FXCollections.observableArrayList(topCustomers));
    }

    private void showTopCars(LocalDateTime startDate, LocalDateTime endDate) {
        List<Map.Entry<String, BigDecimal>> topCars = 
            dealershipService.getTopCars(startDate, endDate);
        
        topCarsTable.setItems(FXCollections.observableArrayList(topCars));
    }

    private String formatCurrency(BigDecimal amount) {
        return String.format("%,.2f ₽", amount);
    }
} 