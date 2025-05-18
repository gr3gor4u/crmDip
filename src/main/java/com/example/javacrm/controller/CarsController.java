package com.example.javacrm.controller;

import com.example.javacrm.model.Car;
import com.example.javacrm.service.CarService;
import com.example.javacrm.service.DatabaseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class CarsController {
    @FXML private TableView<Car> carsTable;
    @FXML private TableColumn<Car, Long> idColumn;
    @FXML private TableColumn<Car, String> brandColumn;
    @FXML private TableColumn<Car, String> modelColumn;
    @FXML private TableColumn<Car, Integer> yearColumn;
    @FXML private TableColumn<Car, Double> priceColumn;
    @FXML private TableColumn<Car, String> statusColumn;
    @FXML private TableColumn<Car, Void> actionsColumn;

    @FXML private TextField vinField;
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField yearField;
    @FXML private TextField colorField;
    @FXML private TextField statusField;

    @FXML private TextField searchVinField;
    @FXML private TextField searchBrandField;
    @FXML private TextField searchModelField;
    @FXML private TextField searchStatusField;

    private CarService carService;
    private ObservableList<Car> carsList;
    private Car selectedCar;

    @FXML
    public void initialize() {
        carService = new CarService(DatabaseService.getInstance());
        
        // Инициализация колонок таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Добавление кнопок действий
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Редактировать");
            private final Button deleteButton = new Button("Удалить");
            
            {
                editButton.setOnAction(e -> {
                    Car car = getTableView().getItems().get(getIndex());
                    handleEditCar(car);
                });
                
                deleteButton.setOnAction(e -> {
                    Car car = getTableView().getItems().get(getIndex());
                    handleDeleteCar(car);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });
        
        refreshTable();
    }

    @FXML
    private void handleAddCar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/car-dialog.fxml"));
            DialogPane dialogPane = loader.load();
            CarDialogController controller = loader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Добавить автомобиль");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setDialogPane(dialogPane);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // TODO: Добавить логику сохранения автомобиля
                refreshTable();
            }
        } catch (IOException e) {
            showError("Ошибка при открытии диалога", e.getMessage());
        }
    }

    private void handleEditCar(Car car) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/car-dialog.fxml"));
            GridPane dialogContent = loader.load();
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Редактировать автомобиль");
            dialog.initModality(Modality.APPLICATION_MODAL);
            
            DialogPane dialogPane = new DialogPane();
            dialogPane.setContent(dialogContent);
            dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            
            dialog.setDialogPane(dialogPane);
            
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // TODO: Добавить логику обновления автомобиля
                refreshTable();
            }
        } catch (IOException e) {
            showError("Ошибка при открытии диалога", e.getMessage());
        }
    }

    private void handleDeleteCar(Car car) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление автомобиля");
        alert.setContentText("Вы уверены, что хотите удалить этот автомобиль?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // TODO: Добавить логику удаления автомобиля
            refreshTable();
        }
    }

    @FXML
    private void handleSearch() {
        // TODO: Реализовать поиск
    }

    @FXML
    private void handleClearSearch() {
        // TODO: Реализовать очистку поиска
    }

    private void refreshTable() {
        carsTable.getItems().clear();
        carsTable.getItems().addAll(carService.getAllCars());
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 