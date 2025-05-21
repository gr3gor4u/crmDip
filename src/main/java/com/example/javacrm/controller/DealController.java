package com.example.javacrm.controller;

import com.example.javacrm.model.Deal;
import com.example.javacrm.model.DealEquipment;
import com.example.javacrm.model.AdditionalEquipment;
import com.example.javacrm.model.Car;
import com.example.javacrm.model.Customer;
import com.example.javacrm.model.User;
import com.example.javacrm.service.DealService;
import com.example.javacrm.service.CustomerService;
import com.example.javacrm.service.CarService;
import com.example.javacrm.service.InsuranceService;
import com.example.javacrm.service.EquipmentService;
import com.example.javacrm.service.UserService;
import com.example.javacrm.service.DatabaseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import java.util.Optional;

public class DealController {
    @FXML
    private ComboBox<Customer> customerComboBox;
    @FXML
    private TextField carVinField;
    @FXML
    private ComboBox<User> managerComboBox;
    @FXML
    private TextField insuranceNumberField;
    @FXML
    private CheckBox noInsuranceCheckBox;
    @FXML
    private CheckBox noEquipmentCheckBox;
    @FXML
    private TableView<DealEquipment> equipmentTable;
    @FXML
    private TableColumn<DealEquipment, String> equipmentNameColumn;
    @FXML
    private TableColumn<DealEquipment, Integer> quantityColumn;
    @FXML
    private TableColumn<DealEquipment, BigDecimal> priceColumn;
    @FXML
    private TableColumn<DealEquipment, Void> editColumn;
    @FXML
    private TextField totalPriceField;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private DatePicker dealDatePicker;
    @FXML
    private ComboBox<AdditionalEquipment> equipmentComboBox;
    @FXML
    private TextField equipmentQuantityField;
    @FXML
    private Button addEquipmentButton;
    
    private DealService dealService;
    private CustomerService customerService;
    private CarService carService;
    private InsuranceService insuranceService;
    private EquipmentService equipmentService;
    private UserService userService;
    private Deal currentDeal;
    private Car selectedCar;
    
    public DealController() {
        DatabaseService databaseService = DatabaseService.getInstance();
        customerService = new CustomerService(databaseService);
        carService = new CarService(databaseService);
        userService = new UserService(databaseService);
        insuranceService = new InsuranceService(databaseService, customerService, carService);
        equipmentService = new EquipmentService(databaseService);
        dealService = new DealService(databaseService, customerService, carService, insuranceService, equipmentService);
    }
    
    @FXML
    public void initialize() {
        dealService = new DealService(DatabaseService.getInstance(), customerService, carService, null, equipmentService);
        customerService = new CustomerService(DatabaseService.getInstance());
        carService = new CarService(DatabaseService.getInstance());
        userService = new UserService(DatabaseService.getInstance());
        equipmentService = new EquipmentService(DatabaseService.getInstance());

        // Заполняем ComboBox'ы
        customerComboBox.setItems(FXCollections.observableArrayList(customerService.getAllCustomers()));
        managerComboBox.setItems(FXCollections.observableArrayList(userService.getAllUsers()));
        statusComboBox.setItems(FXCollections.observableArrayList("Новая", "В процессе", "Завершена", "Отменена"));

        // Настройка отображения в ComboBox'ах
        customerComboBox.setCellFactory(param -> new ListCell<Customer>() {
            @Override
            protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getLastName() + " " + item.getFirstName() + " " + item.getMiddleName());
                }
            }
        });

        managerComboBox.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getLastName() + " " + item.getFirstName());
                }
            }
        });

        // Добавляем отображение для выбранного менеджера
        managerComboBox.setConverter(new StringConverter<User>() {
            @Override
            public String toString(User user) {
                if (user == null) {
                    return "";
                }
                return user.getLastName() + " " + user.getFirstName();
            }

            @Override
            public User fromString(String string) {
                return null;
            }
        });
        
        // Инициализация таблицы оборудования
        equipmentNameColumn.setCellValueFactory(cellData -> {
            DealEquipment equipment = cellData.getValue();
            if (equipment != null && equipment.getEquipment() != null) {
                return new SimpleStringProperty(equipment.getEquipment().getName());
            }
            return new SimpleStringProperty("");
        });

        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Настройка кнопки редактирования
        editColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Редактировать");
            {
                editButton.setOnAction(e -> {
                    DealEquipment equipment = getTableView().getItems().get(getIndex());
                    showEditQuantityDialog(equipment);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });
            
        // Добавляем слушатели для пересчета общей стоимости
        customerComboBox.valueProperty().addListener((obs, oldVal, newVal) -> calculateTotalPrice());
        carVinField.textProperty().addListener((obs, oldVal, newVal) -> calculateTotalPrice());
        insuranceNumberField.textProperty().addListener((obs, oldVal, newVal) -> calculateTotalPrice());
        noEquipmentCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            calculateTotalPrice();
        });
        
        // Устанавливаем текущую дату
        dealDatePicker.setValue(LocalDate.now());

        // Инициализация ComboBox для выбора дополнительного оборудования
        equipmentComboBox.setItems(FXCollections.observableArrayList(equipmentService.getAllEquipment()));
        equipmentComboBox.setCellFactory(param -> new ListCell<AdditionalEquipment>() {
            @Override
            protected void updateItem(AdditionalEquipment item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getPrice() + " руб.)");
                }
            }
        });
        equipmentComboBox.setConverter(new StringConverter<AdditionalEquipment>() {
            @Override
            public String toString(AdditionalEquipment equipment) {
                return equipment == null ? "" : equipment.getName();
            }

            @Override
            public AdditionalEquipment fromString(String string) {
                return null;
            }
        });
    }
    
    @FXML
    private void handleAddEquipment() {
        AdditionalEquipment equipment = equipmentComboBox.getValue();
        if (equipment != null && !equipmentQuantityField.getText().isEmpty()) {
            try {
                int quantity = Integer.parseInt(equipmentQuantityField.getText());
                if (quantity <= 0) {
                    showError("Количество должно быть больше 0");
                    return;
                }
                if (quantity > equipment.getQuantity()) {
                    showError("Количество не может быть больше доступного");
                    return;
                }
                DealEquipment dealEquipment = new DealEquipment();
                dealEquipment.setEquipment(equipment);
                dealEquipment.setQuantity(quantity);
                dealEquipment.setPrice(equipment.getPrice().multiply(BigDecimal.valueOf(quantity)));
                equipmentTable.getItems().add(dealEquipment);
                equipmentQuantityField.clear();
                calculateTotalPrice();
            } catch (NumberFormatException e) {
                showError("Введите корректное число");
            }
        } else {
            showError("Выберите оборудование и укажите количество");
        }
    }
    
    private void calculateTotalPrice() {
        BigDecimal total = BigDecimal.ZERO;
        
        // Добавляем стоимость автомобиля
        String vin = carVinField.getText();
        if (vin != null && !vin.isEmpty()) {
            Car car = carService.getCarByVin(vin);
            if (car != null) {
                total = total.add(car.getPrice());
            }
        }
        
        // Добавляем стоимость оборудования
        if (!noEquipmentCheckBox.isSelected()) {
            for (DealEquipment equipment : equipmentTable.getItems()) {
                total = total.add(equipment.getPrice());
            }
        }
        
        // Добавляем стоимость страховки (если есть)
        if (!noInsuranceCheckBox.isSelected() && !insuranceNumberField.getText().isEmpty()) {
            // Здесь можно добавить логику расчета стоимости страховки
            // Пока просто добавляем фиксированную сумму
            total = total.add(new BigDecimal("10000"));
        }
        
        totalPriceField.setText(total.toString());
    }
    
    @FXML
    private void handleCarVinChange() {
        String vin = carVinField.getText();
        if (vin != null && !vin.isEmpty()) {
            selectedCar = carService.getCarByVin(vin);
            if (selectedCar == null) {
                showError("Автомобиль с таким VIN не найден");
                carVinField.setStyle("-fx-border-color: red;");
            } else {
                carVinField.setStyle("");
                calculateTotalPrice();
            }
        }
    }
    
    @FXML
    private void handleSave() {
        if (isInputValid()) {
            if (currentDeal == null) {
                currentDeal = new Deal();
            }
            
            Customer selectedCustomer = customerComboBox.getValue();
            User selectedManager = managerComboBox.getValue();
            
            if (selectedCar == null) {
                showError("Автомобиль не найден");
                return;
            }
            
            if (selectedCustomer == null) {
                showError("Клиент не выбран");
                return;
            }
            
            if (selectedManager == null) {
                showError("Менеджер не выбран");
                return;
            }
            
            currentDeal.setCar(selectedCar);
            currentDeal.setCarId(selectedCar.getId());
            currentDeal.setCustomer(selectedCustomer);
            currentDeal.setCustomerId(selectedCustomer.getId());
            currentDeal.setManager(selectedManager);
            currentDeal.setManagerId(selectedManager.getId());
            currentDeal.setInsuranceNumber(insuranceNumberField.getText());
            currentDeal.setNoInsurance(noInsuranceCheckBox.isSelected());
            currentDeal.setTotalPrice(new BigDecimal(totalPriceField.getText()));
            currentDeal.setStatus(statusComboBox.getValue());
            currentDeal.setDealDate(dealDatePicker.getValue());
            
            // Добавляем оборудование
            if (!noEquipmentCheckBox.isSelected()) {
                currentDeal.setEquipment(equipmentTable.getItems());
            }
            
            try {
                if (currentDeal.getId() == null) {
                    dealService.createDeal(currentDeal);
                } else {
                    dealService.updateDeal(currentDeal);
                }
                closeDialog();
            } catch (Exception e) {
                showError("Ошибка при сохранении сделки: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleCancel() {
        closeDialog();
    }
    
    private boolean isInputValid() {
        String errorMessage = "";
        
        if (carVinField.getText() == null || carVinField.getText().isEmpty()) {
            errorMessage += "Введите VIN автомобиля!\n";
        }
        if (selectedCar == null) {
            errorMessage += "Автомобиль не найден!\n";
        }
        if (customerComboBox.getValue() == null) {
            errorMessage += "Выберите клиента!\n";
        }
        if (managerComboBox.getValue() == null) {
            errorMessage += "Выберите менеджера!\n";
        }
        if (totalPriceField.getText() == null || totalPriceField.getText().isEmpty()) {
            errorMessage += "Не удалось рассчитать общую стоимость!\n";
        }
        if (statusComboBox.getValue() == null) {
            errorMessage += "Выберите статус сделки!\n";
        }
        if (dealDatePicker.getValue() == null) {
            errorMessage += "Выберите дату сделки!\n";
        }
        
        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showError(errorMessage);
            return false;
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void closeDialog() {
        ((Stage) carVinField.getScene().getWindow()).close();
    }

    private void showEditQuantityDialog(DealEquipment equipment) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Изменить количество");
        dialog.setHeaderText("Введите новое количество для " + equipment.getEquipment().getName());

        ButtonType saveButtonType = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField quantityField = new TextField(equipment.getQuantity().toString());
        quantityField.setPromptText("Количество");

        dialog.getDialogPane().setContent(quantityField);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int newQuantity = Integer.parseInt(quantityField.getText());
                    if (newQuantity <= 0) {
                        showError("Количество должно быть больше 0");
                        return null;
                    }
                    if (newQuantity > equipment.getEquipment().getQuantity()) {
                        showError("Количество не может быть больше доступного");
                        return null;
                    }
                    return newQuantity;
                } catch (NumberFormatException e) {
                    showError("Введите корректное число");
                    return null;
                }
            }
            return null;
        });

        Optional<Integer> result = dialog.showAndWait();
        result.ifPresent(newQuantity -> {
            equipment.setQuantity(newQuantity);
            equipment.setPrice(equipment.getEquipment().getPrice().multiply(BigDecimal.valueOf(newQuantity)));
            equipmentTable.refresh();
            calculateTotalPrice();
        });
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успех");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 