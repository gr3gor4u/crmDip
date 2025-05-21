package com.example.javacrm.controller;

import com.example.javacrm.model.Insurance;
import com.example.javacrm.service.InsuranceService;
import com.example.javacrm.service.CustomerService;
import com.example.javacrm.service.CarService;
import com.example.javacrm.service.DatabaseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InsuranceController {
    @FXML
    private TableView<Insurance> insurancesTable;
    @FXML
    private TableColumn<Insurance, Long> idColumn;
    @FXML
    private TableColumn<Insurance, String> numberColumn;
    @FXML
    private TableColumn<Insurance, String> customerColumn;
    @FXML
    private TableColumn<Insurance, String> carColumn;
    @FXML
    private TableColumn<Insurance, String> typeColumn;
    @FXML
    private TableColumn<Insurance, LocalDate> startDateColumn;
    @FXML
    private TableColumn<Insurance, LocalDate> expiryDateColumn;
    @FXML
    private TableColumn<Insurance, String> statusColumn;

    @FXML
    private TextField numberSearchField;
    @FXML
    private TextField vinSearchField;
    @FXML
    private TextField customerSearchField;
    @FXML
    private ComboBox<Insurance.InsuranceType> typeFilterComboBox;
    @FXML
    private ComboBox<String> statusFilterComboBox;

    private final InsuranceService insuranceService;
    private final ObservableList<Insurance> insurancesList = FXCollections.observableArrayList();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public InsuranceController() {
        DatabaseService databaseService = DatabaseService.getInstance();
        CustomerService customerService = new CustomerService(databaseService);
        CarService carService = new CarService(databaseService);
        this.insuranceService = new InsuranceService(databaseService, customerService, carService);
    }

    @FXML
    private void initialize() {
        // Инициализация колонок таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("insuranceNumber"));
        customerColumn.setCellValueFactory(cellData -> {
            Insurance insurance = cellData.getValue();
            return new SimpleStringProperty(
                insurance.getCustomerFirstName() + " " + insurance.getCustomerLastName()
            );
        });
        carColumn.setCellValueFactory(cellData -> {
            Insurance insurance = cellData.getValue();
            return new SimpleStringProperty(
                insurance.getCarBrand() + " " + insurance.getCarModel()
            );
        });
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("insuranceType"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        expiryDateColumn.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Форматирование дат
        startDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(dateFormatter.format(date));
                }
            }
        });

        expiryDateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(dateFormatter.format(date));
                }
            }
        });

        // Инициализация фильтров
        typeFilterComboBox.getItems().addAll(Insurance.InsuranceType.values());
        typeFilterComboBox.getItems().add(0, null);
        typeFilterComboBox.setValue(null);

        statusFilterComboBox.getItems().addAll("Все", "Активна", "Истекла");
        statusFilterComboBox.setValue("Все");

        // Установка данных в таблицу
        insurancesTable.setItems(insurancesList);
        refreshInsuranceList();
    }

    @FXML
    private void handleSearch() {
        String number = numberSearchField.getText();
        String vin = vinSearchField.getText();
        String customer = customerSearchField.getText();
        Insurance.InsuranceType type = typeFilterComboBox.getValue();
        String status = statusFilterComboBox.getValue();

        insurancesList.clear();
        insurancesList.addAll(insuranceService.searchInsurances(number, vin, customer, type, status));
    }

    @FXML
    private void handleReset() {
        numberSearchField.clear();
        vinSearchField.clear();
        customerSearchField.clear();
        typeFilterComboBox.setValue(null);
        statusFilterComboBox.setValue("Все");
        refreshInsuranceList();
    }

    private void refreshInsuranceList() {
        insurancesList.clear();
        insurancesList.addAll(insuranceService.getAllInsurances());
    }

    @FXML
    private void handleAddInsurance() {
        Insurance insurance = new Insurance();
        boolean okClicked = showInsuranceDialog(insurance);
        if (okClicked) {
            insuranceService.createInsurance(insurance);
            refreshInsuranceList();
        }
    }

    @FXML
    private void handleEditInsurance() {
        Insurance selectedInsurance = insurancesTable.getSelectionModel().getSelectedItem();
        if (selectedInsurance != null) {
            boolean okClicked = showInsuranceDialog(selectedInsurance);
            if (okClicked) {
                insuranceService.updateInsurance(selectedInsurance);
                refreshInsuranceList();
            }
        } else {
            showAlert("Ошибка", "Пожалуйста, выберите страховку для редактирования.");
        }
    }

    @FXML
    private void handleDeleteInsurance() {
        Insurance selectedInsurance = insurancesTable.getSelectionModel().getSelectedItem();
        if (selectedInsurance != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Подтверждение удаления");
            alert.setHeaderText("Удаление страховки");
            alert.setContentText("Вы уверены, что хотите удалить эту страховку?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                insuranceService.deleteInsurance(selectedInsurance.getId());
                refreshInsuranceList();
            }
        } else {
            showAlert("Ошибка", "Пожалуйста, выберите страховку для удаления.");
        }
    }

    private boolean showInsuranceDialog(Insurance insurance) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/insurance-dialog.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Страховка");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(insurancesTable.getScene().getWindow());

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            InsuranceDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setInsurance(insurance);

            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 