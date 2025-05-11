package com.example.javacrm.controller;

import com.example.javacrm.model.Car;
import com.example.javacrm.model.CarStatus;
import com.example.javacrm.service.CarService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class CarsController {

    @Autowired
    private CarService carService;

    @FXML
    private TableView<Car> carsTable;
    @FXML
    private TableColumn<Car, String> vinColumn;
    @FXML
    private TableColumn<Car, String> brandColumn;
    @FXML
    private TableColumn<Car, String> modelColumn;
    @FXML
    private TableColumn<Car, Integer> yearColumn;
    @FXML
    private TableColumn<Car, String> colorColumn;
    @FXML
    private TableColumn<Car, BigDecimal> priceColumn;
    @FXML
    private TableColumn<Car, CarStatus> statusColumn;
    @FXML
    private TableColumn<Car, Integer> mileageColumn;
    @FXML
    private TableColumn<Car, Void> actionsColumn;

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> brandFilter;
    @FXML
    private ComboBox<CarStatus> statusFilter;

    @FXML
    private Dialog<Car> carDialog;
    @FXML
    private TextField vinField;
    @FXML
    private TextField brandField;
    @FXML
    private TextField modelField;
    @FXML
    private TextField yearField;
    @FXML
    private TextField colorField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField bodyTypeField;
    @FXML
    private TextField engineVolumeField;
    @FXML
    private TextField horsePowerField;
    @FXML
    private TextField mileageField;
    @FXML
    private ComboBox<CarStatus> statusField;
    @FXML
    private TextArea descriptionField;

    private ObservableList<Car> cars = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Инициализация таблицы
        vinColumn.setCellValueFactory(new PropertyValueFactory<>("vinNumber"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        colorColumn.setCellValueFactory(new PropertyValueFactory<>("color"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        mileageColumn.setCellValueFactory(new PropertyValueFactory<>("mileage"));

        // Добавление кнопок действий
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("✎");
            private final Button deleteButton = new Button("✕");

            {
                editButton.setOnAction(event -> {
                    Car car = getTableView().getItems().get(getIndex());
                    showEditCarDialog(car);
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
        });

        // Инициализация фильтров
        brandFilter.setItems(FXCollections.observableArrayList(carService.getAllBrands()));
        statusFilter.setItems(FXCollections.observableArrayList(CarStatus.values()));
        statusField.setItems(FXCollections.observableArrayList(CarStatus.values()));

        // Загрузка данных
        refreshCars();
    }

    @FXML
    private void searchCars() {
        String searchText = searchField.getText();
        String brand = brandFilter.getValue();
        CarStatus status = statusFilter.getValue();

        List<Car> filteredCars = carService.searchCars(searchText, brand, status);
        cars.setAll(filteredCars);
    }

    @FXML
    private void resetSearch() {
        searchField.clear();
        brandFilter.setValue(null);
        statusFilter.setValue(null);
        refreshCars();
    }

    @FXML
    private void showAddCarDialog() {
        carDialog.setTitle("Добавить автомобиль");
        clearDialogFields();
        
        Optional<ButtonType> result = carDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Car car = createCarFromDialog();
                carService.createCar(car);
                refreshCars();
            } catch (Exception e) {
                showError("Ошибка при создании автомобиля", e.getMessage());
            }
        }
    }

    private void showEditCarDialog(Car car) {
        carDialog.setTitle("Редактировать автомобиль");
        fillDialogFields(car);
        
        Optional<ButtonType> result = carDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Car updatedCar = createCarFromDialog();
                updatedCar.setId(car.getId());
                carService.updateCar(car.getId(), updatedCar);
                refreshCars();
            } catch (Exception e) {
                showError("Ошибка при обновлении автомобиля", e.getMessage());
            }
        }
    }

    private Car createCarFromDialog() {
        Car car = new Car();
        car.setVinNumber(vinField.getText());
        car.setBrand(brandField.getText());
        car.setModel(modelField.getText());
        car.setYear(Integer.parseInt(yearField.getText()));
        car.setColor(colorField.getText());
        car.setPrice(new BigDecimal(priceField.getText()));
        car.setBodyType(bodyTypeField.getText());
        car.setEngineVolume(Double.parseDouble(engineVolumeField.getText()));
        car.setHorsePower(Integer.parseInt(horsePowerField.getText()));
        car.setMileage(Integer.parseInt(mileageField.getText()));
        car.setStatus(statusField.getValue());
        car.setDescription(descriptionField.getText());
        return car;
    }

    private void deleteCar(Car car) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удалить автомобиль?");
        alert.setContentText("Вы уверены, что хотите удалить автомобиль " + car.getBrand() + " " + car.getModel() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                carService.deleteCar(car.getId());
                refreshCars();
            } catch (Exception e) {
                showError("Ошибка при удалении автомобиля", e.getMessage());
            }
        }
    }

    private void refreshCars() {
        List<Car> allCars = carService.getAllCars();
        cars.setAll(allCars);
        carsTable.setItems(cars);
    }

    private void clearDialogFields() {
        vinField.clear();
        brandField.clear();
        modelField.clear();
        yearField.clear();
        colorField.clear();
        priceField.clear();
        bodyTypeField.clear();
        engineVolumeField.clear();
        horsePowerField.clear();
        mileageField.clear();
        statusField.setValue(CarStatus.AVAILABLE);
        descriptionField.clear();
    }

    private void fillDialogFields(Car car) {
        vinField.setText(car.getVinNumber());
        brandField.setText(car.getBrand());
        modelField.setText(car.getModel());
        yearField.setText(String.valueOf(car.getYear()));
        colorField.setText(car.getColor());
        priceField.setText(car.getPrice().toString());
        bodyTypeField.setText(car.getBodyType());
        engineVolumeField.setText(car.getEngineVolume().toString());
        horsePowerField.setText(String.valueOf(car.getHorsePower()));
        mileageField.setText(String.valueOf(car.getMileage()));
        statusField.setValue(car.getStatus());
        descriptionField.setText(car.getDescription());
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 
        vinField.setText(car.getVinNumber());
        brandField.setText(car.getBrand());
        modelField.setText(car.getModel());
        yearField.setText(String.valueOf(car.getYear()));
        colorField.setText(car.getColor());
        priceField.setText(car.getPrice().toString());
        bodyTypeField.setText(car.getBodyType());
        engineVolumeField.setText(car.getEngineVolume().toString());
        horsePowerField.setText(String.valueOf(car.getHorsePower()));
        mileageField.setText(String.valueOf(car.getMileage()));
        statusField.setValue(car.getStatus());
        descriptionField.setText(car.getDescription());
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 