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
import javafx.geometry.Insets;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class CarsController {
    @FXML private TableView<Car> carsTable;
    @FXML private TableColumn<Car, Long> idColumn;
    @FXML private TableColumn<Car, String> vinColumn;
    @FXML private TableColumn<Car, String> brandColumn;
    @FXML private TableColumn<Car, String> modelColumn;
    @FXML private TableColumn<Car, Integer> yearColumn;
    @FXML private TableColumn<Car, Double> priceColumn;
    @FXML private TableColumn<Car, String> colorColumn;
    @FXML private TableColumn<Car, String> kuzovColumn;
    @FXML private TableColumn<Car, Double> engineVolumeColumn;
    @FXML private TableColumn<Car, Integer> horsePowerColumn;
    @FXML private TableColumn<Car, String> statusColumn;
    @FXML private TableColumn<Car, Void> actionsColumn;

    @FXML private TextField vinFilterField;
    @FXML private TextField brandFilterField;
    @FXML private TextField modelFilterField;
    @FXML private TextField colorFilterField;
    @FXML private ComboBox<String> statusFilterComboBox;

    private CarService carService;
    private ObservableList<Car> carsList;
    private Car selectedCar;

    @FXML
    public void initialize() {
        carService = new CarService(DatabaseService.getInstance());
        
        // Инициализация колонок таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        vinColumn.setCellValueFactory(new PropertyValueFactory<>("vin"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        kuzovColumn.setCellValueFactory(new PropertyValueFactory<>("kuzov"));
        engineVolumeColumn.setCellValueFactory(new PropertyValueFactory<>("engineVolume"));
        horsePowerColumn.setCellValueFactory(new PropertyValueFactory<>("horsePower"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Инициализация ComboBox для статуса
        statusFilterComboBox.getItems().clear();
        statusFilterComboBox.getItems().addAll("", "В наличии", "Забронирован", "Продан", "На ремонте");
        statusFilterComboBox.setValue("");

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
                    buttons.setPadding(new Insets(5));
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
                Car newCar = controller.getCar();
                carService.addCar(newCar);
                refreshTable();
            }
        } catch (IOException e) {
            showError("Ошибка при открытии диалога", e.getMessage());
        }
    }

    private void handleEditCar(Car car) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/car-dialog.fxml"));
            DialogPane dialogPane = loader.load();
            CarDialogController controller = loader.getController();
            controller.setCar(car);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Редактировать автомобиль");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setDialogPane(dialogPane);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Car updatedCar = controller.getCar();
                if (updatedCar != null) {
                    updatedCar.setId(car.getId());
                    carService.updateCar(updatedCar);
                    refreshTable();
                }
            }
        } catch (IOException e) {
            showError("Ошибка при открытии диалога", e.getMessage());
        }
    }

    private void handleDeleteCar(Car car) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление автомобиля");
        alert.setContentText("Вы уверены, что хотите удалить автомобиль " + car.getBrand() + " " + car.getModel() + "?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            carService.deleteCar(car.getId());
            refreshTable();
        }
    }

    @FXML
    private void handleSearch() {
        String vin = vinFilterField.getText().trim();
        String brand = brandFilterField.getText().trim();
        String model = modelFilterField.getText().trim();
        String color = colorFilterField.getText().trim();
        String status = statusFilterComboBox.getValue();

        carsTable.getItems().clear();
        carsTable.getItems().addAll(carService.searchCars(vin, brand, model, color, status));
    }

    @FXML
    private void handleClearSearch() {
        vinFilterField.clear();
        brandFilterField.clear();
        modelFilterField.clear();
        colorFilterField.clear();
        statusFilterComboBox.setValue("");
        refreshTable();
    }

    private void refreshTable() {
        carsList = FXCollections.observableArrayList(carService.getAllCars());
        carsTable.setItems(carsList);
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 