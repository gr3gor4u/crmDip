package com.example.javacrm.controller;

import com.example.javacrm.model.Deal;
import com.example.javacrm.service.DealershipService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class DashboardController {
    @FXML private Label totalSalesLabel;
    @FXML private Label carsSoldLabel;
    @FXML private Label availableCarsLabel;
    @FXML private Label activeDealsLabel;
    
    @FXML private TableView<Deal> recentDealsTable;
    @FXML private TableColumn<Deal, Long> dealIdColumn;
    @FXML private TableColumn<Deal, String> customerColumn;
    @FXML private TableColumn<Deal, String> carColumn;
    @FXML private TableColumn<Deal, String> amountColumn;
    @FXML private TableColumn<Deal, String> statusColumn;
    @FXML private TableColumn<Deal, String> dateColumn;
    
    @FXML private LineChart<String, Number> salesChart;

    private final DealershipService dealershipService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    @Autowired
    public DashboardController(DealershipService dealershipService) {
        this.dealershipService = dealershipService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        refreshDashboard();
    }

    private void setupTableColumns() {
        dealIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(
                cellData.getValue().getCustomer().getFullName()
            )
        );
        carColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(
                cellData.getValue().getCar().getBrand() + " " + 
                cellData.getValue().getCar().getModel()
            )
        );
        amountColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(
                formatCurrency(cellData.getValue().getAmount())
            )
        );
        statusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(
                cellData.getValue().getStatus().toString()
            )
        );
        dateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(
                cellData.getValue().getCreatedAt().format(dateFormatter)
            )
        );
    }

    @FXML
    public void refreshDashboard() {
        // Обновляем статистику
        totalSalesLabel.setText(formatCurrency(dealershipService.getTotalSales()));
        carsSoldLabel.setText(String.valueOf(dealershipService.getCarsSold()));
        availableCarsLabel.setText(String.valueOf(dealershipService.getAvailableCars()));
        activeDealsLabel.setText(String.valueOf(dealershipService.getPendingDeals()));

        // Обновляем таблицу последних сделок
        List<Deal> recentDeals = dealershipService.getRecentDeals();
        recentDealsTable.setItems(FXCollections.observableArrayList(recentDeals));

        // Обновляем график продаж
        updateSalesChart();
    }

    private void updateSalesChart() {
        salesChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Продажи по месяцам");

        // TODO: Реализовать получение данных о продажах по месяцам
        // Временные данные для примера
        series.getData().add(new XYChart.Data<>("Янв", 100000));
        series.getData().add(new XYChart.Data<>("Фев", 150000));
        series.getData().add(new XYChart.Data<>("Мар", 200000));
        series.getData().add(new XYChart.Data<>("Апр", 180000));
        series.getData().add(new XYChart.Data<>("Май", 250000));
        series.getData().add(new XYChart.Data<>("Июн", 300000));

        salesChart.getData().add(series);
    }

    private String formatCurrency(BigDecimal amount) {
        return String.format("%,.2f ₽", amount);
    }
} 