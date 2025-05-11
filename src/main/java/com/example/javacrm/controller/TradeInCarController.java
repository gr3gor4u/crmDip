package com.example.javacrm.controller;

import com.example.javacrm.model.TradeInCar;
import com.example.javacrm.service.TradeInCarService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.beans.property.SimpleStringProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class TradeInCarController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> brandFilter;
    
    @FXML private TableView<TradeInCar> tradeInTable;
    @FXML private TableColumn<TradeInCar, String> vinColumn;
    @FXML private TableColumn<TradeInCar, String> brandColumn;
    @FXML private TableColumn<TradeInCar, String> modelColumn;
    @FXML private TableColumn<TradeInCar, String> yearColumn;
    @FXML private TableColumn<TradeInCar, String> mileageColumn;
    @FXML private TableColumn<TradeInCar, Void> actionsColumn;

    @FXML private Dialog<TradeInCar> tradeInDialog;
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField yearField;
    @FXML private TextField mileageField;
    @FXML private TextArea descriptionField;
    @FXML private TextField vinField;

    private final TradeInCarService tradeInCarService;

    @Autowired
    public TradeInCarController(TradeInCarService tradeInCarService) {
        this.tradeInCarService = tradeInCarService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupActionsColumn();
        loadTradeInCars();
        setupFilters();
    }

    private void setupTableColumns() {
        vinColumn.setCellValueFactory(new PropertyValueFactory<>("vinNumber"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        mileageColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatMileage(cellData.getValue().getMileage()))
        );
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(createActionButtonsCellFactory());
    }

    private Callback<TableColumn<TradeInCar, Void>, TableCell<TradeInCar, Void>> createActionButtonsCellFactory() {
        return param -> new TableCell<>() {
            private final Button editButton = new Button("✎");
            private final Button deleteButton = new Button("✕");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    TradeInCar car = getTableView().getItems().get(getIndex());
                    showEditTradeInCarDialog(car);
                });

                deleteButton.setOnAction(event -> {
                    TradeInCar car = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(car);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        };
    }

    private void setupFilters() {
        List<TradeInCar> cars = tradeInCarService.findAll();
        brandFilter.setItems(FXCollections.observableArrayList(
            cars.stream()
                .map(TradeInCar::getBrand)
                .distinct()
                .toList()
        ));
    }

    @FXML
    private void loadTradeInCars() {
        List<TradeInCar> cars = tradeInCarService.findAll();
        tradeInTable.setItems(FXCollections.observableArrayList(cars));
    }

    @FXML
    private void searchTradeInCars() {
        String searchQuery = searchField.getText();
        String selectedBrand = brandFilter.getValue();

        List<TradeInCar> cars = tradeInCarService.searchTradeInCars(searchQuery, selectedBrand, null);
        tradeInTable.setItems(FXCollections.observableArrayList(cars));
    }

    @FXML
    private void resetSearch() {
        searchField.clear();
        brandFilter.setValue(null);
        loadTradeInCars();
    }

    @FXML
    private void showAddTradeInCarDialog() {
        clearDialogFields();
        tradeInDialog.setTitle("Добавить автомобиль на трейд-ин");
        
        tradeInDialog.showAndWait().ifPresent(car -> {
            tradeInCarService.save(car);
            loadTradeInCars();
            setupFilters();
        });
    }

    private void showEditTradeInCarDialog(TradeInCar car) {
        fillDialogFields(car);
        tradeInDialog.setTitle("Редактировать автомобиль на трейд-ин");
        
        tradeInDialog.showAndWait().ifPresent(updatedCar -> {
            updatedCar.setVinNumber(car.getVinNumber());
            tradeInCarService.save(updatedCar);
            loadTradeInCars();
            setupFilters();
        });
    }

    private void showDeleteConfirmation(TradeInCar car) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление автомобиля на трейд-ин");
        alert.setContentText("Вы уверены, что хотите удалить автомобиль " + 
                           car.getBrand() + " " + car.getModel() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                tradeInCarService.deleteById(car.getVinNumber());
                loadTradeInCars();
                setupFilters();
            }
        });
    }

    private void clearDialogFields() {
        brandField.clear();
        modelField.clear();
        yearField.clear();
        mileageField.clear();
        descriptionField.clear();
        vinField.clear();
    }

    private void fillDialogFields(TradeInCar car) {
        brandField.setText(car.getBrand());
        modelField.setText(car.getModel());
        yearField.setText(String.valueOf(car.getYear()));
        mileageField.setText(String.valueOf(car.getMileage()));
        descriptionField.setText(car.getDescription());
        vinField.setText(car.getVinNumber());
    }

    private TradeInCar createTradeInCarFromDialog() {
        TradeInCar car = new TradeInCar();
        car.setVinNumber(vinField.getText());
        car.setBrand(brandField.getText());
        car.setModel(modelField.getText());
        car.setYear(Integer.parseInt(yearField.getText()));
        car.setMileage(Integer.parseInt(mileageField.getText()));
        car.setDescription(descriptionField.getText());
        return car;
    }

    private String formatCurrency(BigDecimal amount) {
        return String.format("%,.2f ₽", amount);
    }

    private String formatMileage(int mileage) {
        return String.format("%,d км", mileage);
    }
} 

import com.example.javacrm.model.TradeInCar;
import com.example.javacrm.service.TradeInCarService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.beans.property.SimpleStringProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class TradeInCarController {
    @FXML private TextField searchField;
    @FXML private ComboBox<String> brandFilter;
    
    @FXML private TableView<TradeInCar> tradeInTable;
    @FXML private TableColumn<TradeInCar, String> vinColumn;
    @FXML private TableColumn<TradeInCar, String> brandColumn;
    @FXML private TableColumn<TradeInCar, String> modelColumn;
    @FXML private TableColumn<TradeInCar, String> yearColumn;
    @FXML private TableColumn<TradeInCar, String> mileageColumn;
    @FXML private TableColumn<TradeInCar, Void> actionsColumn;

    @FXML private Dialog<TradeInCar> tradeInDialog;
    @FXML private TextField brandField;
    @FXML private TextField modelField;
    @FXML private TextField yearField;
    @FXML private TextField mileageField;
    @FXML private TextArea descriptionField;
    @FXML private TextField vinField;

    private final TradeInCarService tradeInCarService;

    @Autowired
    public TradeInCarController(TradeInCarService tradeInCarService) {
        this.tradeInCarService = tradeInCarService;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupActionsColumn();
        loadTradeInCars();
        setupFilters();
    }

    private void setupTableColumns() {
        vinColumn.setCellValueFactory(new PropertyValueFactory<>("vinNumber"));
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brand"));
        modelColumn.setCellValueFactory(new PropertyValueFactory<>("model"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        mileageColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatMileage(cellData.getValue().getMileage()))
        );
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(createActionButtonsCellFactory());
    }

    private Callback<TableColumn<TradeInCar, Void>, TableCell<TradeInCar, Void>> createActionButtonsCellFactory() {
        return param -> new TableCell<>() {
            private final Button editButton = new Button("✎");
            private final Button deleteButton = new Button("✕");
            private final HBox buttons = new HBox(5, editButton, deleteButton);

            {
                editButton.setOnAction(event -> {
                    TradeInCar car = getTableView().getItems().get(getIndex());
                    showEditTradeInCarDialog(car);
                });

                deleteButton.setOnAction(event -> {
                    TradeInCar car = getTableView().getItems().get(getIndex());
                    showDeleteConfirmation(car);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        };
    }

    private void setupFilters() {
        List<TradeInCar> cars = tradeInCarService.findAll();
        brandFilter.setItems(FXCollections.observableArrayList(
            cars.stream()
                .map(TradeInCar::getBrand)
                .distinct()
                .toList()
        ));
    }

    @FXML
    private void loadTradeInCars() {
        List<TradeInCar> cars = tradeInCarService.findAll();
        tradeInTable.setItems(FXCollections.observableArrayList(cars));
    }

    @FXML
    private void searchTradeInCars() {
        String searchQuery = searchField.getText();
        String selectedBrand = brandFilter.getValue();

        List<TradeInCar> cars = tradeInCarService.searchTradeInCars(searchQuery, selectedBrand, null);
        tradeInTable.setItems(FXCollections.observableArrayList(cars));
    }

    @FXML
    private void resetSearch() {
        searchField.clear();
        brandFilter.setValue(null);
        loadTradeInCars();
    }

    @FXML
    private void showAddTradeInCarDialog() {
        clearDialogFields();
        tradeInDialog.setTitle("Добавить автомобиль на трейд-ин");
        
        tradeInDialog.showAndWait().ifPresent(car -> {
            tradeInCarService.save(car);
            loadTradeInCars();
            setupFilters();
        });
    }

    private void showEditTradeInCarDialog(TradeInCar car) {
        fillDialogFields(car);
        tradeInDialog.setTitle("Редактировать автомобиль на трейд-ин");
        
        tradeInDialog.showAndWait().ifPresent(updatedCar -> {
            updatedCar.setVinNumber(car.getVinNumber());
            tradeInCarService.save(updatedCar);
            loadTradeInCars();
            setupFilters();
        });
    }

    private void showDeleteConfirmation(TradeInCar car) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление автомобиля на трейд-ин");
        alert.setContentText("Вы уверены, что хотите удалить автомобиль " + 
                           car.getBrand() + " " + car.getModel() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                tradeInCarService.deleteById(car.getVinNumber());
                loadTradeInCars();
                setupFilters();
            }
        });
    }

    private void clearDialogFields() {
        brandField.clear();
        modelField.clear();
        yearField.clear();
        mileageField.clear();
        descriptionField.clear();
        vinField.clear();
    }

    private void fillDialogFields(TradeInCar car) {
        brandField.setText(car.getBrand());
        modelField.setText(car.getModel());
        yearField.setText(String.valueOf(car.getYear()));
        mileageField.setText(String.valueOf(car.getMileage()));
        descriptionField.setText(car.getDescription());
        vinField.setText(car.getVinNumber());
    }

    private TradeInCar createTradeInCarFromDialog() {
        TradeInCar car = new TradeInCar();
        car.setVinNumber(vinField.getText());
        car.setBrand(brandField.getText());
        car.setModel(modelField.getText());
        car.setYear(Integer.parseInt(yearField.getText()));
        car.setMileage(Integer.parseInt(mileageField.getText()));
        car.setDescription(descriptionField.getText());
        return car;
    }

    private String formatCurrency(BigDecimal amount) {
        return String.format("%,.2f ₽", amount);
    }

    private String formatMileage(int mileage) {
        return String.format("%,d км", mileage);
    }
} 