package com.example.javacrm.controller;

import com.example.javacrm.model.Car;
import com.example.javacrm.service.CarService;
import com.example.javacrm.service.DatabaseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.control.cell.PropertyValueFactory;

public class CarsController {
    @FXML private TableView<Car> carsTable;
    @FXML private TableColumn<Car, String> modelColumn;
    @FXML private TableColumn<Car, String> brandColumn;
    @FXML private TableColumn<Car, Integer> yearColumn;
    @FXML private TableColumn<Car, Double> priceColumn;
    @FXML private TableColumn<Car, String> statusColumn;
    @FXML private TableColumn<Car, Void> actionsColumn;
    @FXML private TextField modelField;
    @FXML private TextField brandField;
    @FXML private TextField yearField;
    @FXML private TextField priceField;
    @FXML private ComboBox<String> statusComboBox;

    private final CarService carService;
    private final ObservableList<Car> cars = FXCollections.observableArrayList();

    public CarsController() {
        this.carService = new CarService(DatabaseService.getInstance());
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupStatusComboBox();
        loadCars();
    }

    private void setupTableColumns() {
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("carYear"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Настройка отображения статуса с цветом
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "AVAILABLE":
                            setTextFill(Color.GREEN);
                            break;
                        case "NOT_AVAILABLE":
                            setTextFill(Color.ORANGE);
                            break;
                        case "SOLD":
                            setTextFill(Color.RED);
                            break;
                    }
                }
            }
        });

        // Настройка кнопок действий
        actionsColumn.setCellFactory(createActionButtonsCellFactory());

        // Настройка стиля строк в зависимости от статуса
        carsTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Car car, boolean empty) {
                super.updateItem(car, empty);
                if (car == null || empty) {
                    setStyle("");
                } else if ("SOLD".equals(car.getStatus())) {
                    setStyle("-fx-background-color: #eeeeee; -fx-text-fill: #888888;");
                } else if ("NOT_AVAILABLE".equals(car.getStatus())) {
                    setStyle("-fx-opacity: 0.5;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private Callback<TableColumn<Car, Void>, TableCell<Car, Void>> createActionButtonsCellFactory() {
        return column -> new TableCell<>() {
            private final Button editButton = new Button("Изменить статус");
            private final Button deleteButton = new Button("Удалить");

            {
                editButton.setOnAction(event -> {
                    Car car = getTableView().getItems().get(getIndex());
                    showStatusChangeDialog(car);
                });

                deleteButton.setOnAction(event -> {
                    Car car = getTableView().getItems().get(getIndex());
                    deleteCar(car);
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
        };
    }

    private void setupStatusComboBox() {
        statusComboBox.setItems(FXCollections.observableArrayList(
            "AVAILABLE", "NOT_AVAILABLE", "SOLD"
        ));
        statusComboBox.getSelectionModel().selectFirst();
    }

    private void loadCars() {
        cars.clear();
        cars.addAll(carService.getAllCars());
        carsTable.setItems(cars);
    }

    @FXML
    private void handleAddCar() {
        // Очищаем поля
        modelField.clear();
        brandField.clear();
        yearField.clear();
        priceField.clear();
        statusComboBox.getSelectionModel().selectFirst();

        // Создаём кастомный диалог
        Dialog<Car> dialog = new Dialog<>();
        dialog.setTitle("Добавить автомобиль");
        
        // Создаём контент диалога
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(new Label("Модель:"), 0, 0);
        grid.add(modelField, 1, 0);
        grid.add(new Label("Марка:"), 0, 1);
        grid.add(brandField, 1, 1);
        grid.add(new Label("Год:"), 0, 2);
        grid.add(yearField, 1, 2);
        grid.add(new Label("Цена:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(new Label("Статус:"), 0, 4);
        grid.add(statusComboBox, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                try {
                    String model = modelField.getText();
                    String brand = brandField.getText();
                    int year = Integer.parseInt(yearField.getText());
                    double price = Double.parseDouble(priceField.getText());
                    String status = statusComboBox.getValue();

                    if (model.isEmpty() || brand.isEmpty()) {
                        showError("Ошибка", "Пожалуйста, заполните все поля");
                        return null;
                    }

                    return new Car(null, model, brand, year, price, status);
                } catch (NumberFormatException e) {
                    showError("Ошибка", "Пожалуйста, введите корректные числовые значения для года и цены");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(car -> {
            if (car != null) {
                carService.addCar(car);
                loadCars();
            }
        });
    }

    private void showStatusChangeDialog(Car car) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Изменить статус");
        dialog.setHeaderText("Выберите новый статус для автомобиля " + car.getBrand() + " " + car.getModel());

        ComboBox<String> statusBox = new ComboBox<>(FXCollections.observableArrayList(
            "AVAILABLE", "NOT_AVAILABLE", "SOLD"
        ));
        statusBox.setValue(car.getStatus());

        dialog.getDialogPane().setContent(statusBox);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return statusBox.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newStatus -> {
            carService.updateCarStatus(car.getId(), newStatus);
            loadCars();
        });
    }

    private void deleteCar(Car car) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удалить автомобиль " + car.getBrand() + " " + car.getModel() + "?");
        alert.setContentText("Это действие нельзя отменить.");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                carService.deleteCar(car.getId());
                loadCars();
            }
        });
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 