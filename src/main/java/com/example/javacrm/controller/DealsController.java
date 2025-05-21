package com.example.javacrm.controller;

import com.example.javacrm.model.Deal;
import com.example.javacrm.model.DealEquipment;
import com.example.javacrm.service.DealService;
import com.example.javacrm.service.CustomerService;
import com.example.javacrm.service.CarService;
import com.example.javacrm.service.InsuranceService;
import com.example.javacrm.service.EquipmentService;
import com.example.javacrm.service.DatabaseService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.beans.property.SimpleStringProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.io.IOException;
import java.util.Optional;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Region;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import java.util.List;

public class DealsController {
    @FXML
    private TableView<Deal> dealsTable;
    @FXML
    private TableColumn<Deal, Long> idColumn;
    @FXML
    private TableColumn<Deal, String> customerColumn;
    @FXML
    private TableColumn<Deal, String> carColumn;
    @FXML
    private TableColumn<Deal, Double> amountColumn;
    @FXML
    private TableColumn<Deal, String> dateColumn;
    @FXML
    private TableColumn<Deal, String> statusColumn;
    @FXML
    private TableColumn<Deal, Void> actionsColumn;
    
    @FXML
    private TextField searchCustomerField;
    @FXML
    private TextField searchCarField;
    @FXML
    private ComboBox<String> searchStatusComboBox;
    
    private DealService dealService;
    private CustomerService customerService;
    private CarService carService;
    private InsuranceService insuranceService;
    private EquipmentService equipmentService;
    private ObservableList<Deal> dealsList;
    private Deal selectedDeal;
    
    @FXML
    public void initialize() {
        DatabaseService databaseService = DatabaseService.getInstance();
        customerService = new CustomerService(databaseService);
        carService = new CarService(databaseService);
        insuranceService = new InsuranceService(databaseService, customerService, carService);
        equipmentService = new EquipmentService(databaseService);
        dealService = new DealService(databaseService, customerService, carService, insuranceService, equipmentService);
        
        // Инициализация колонок таблицы
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerColumn.setCellValueFactory(cellData -> {
            Deal deal = cellData.getValue();
            String fullName = deal.getCustomer().getLastName() + " " + 
                            deal.getCustomer().getFirstName() + " " + 
                            (deal.getCustomer().getMiddleName() != null ? deal.getCustomer().getMiddleName() : "");
            return new SimpleStringProperty(fullName);
        });
        carColumn.setCellValueFactory(cellData -> {
            Deal deal = cellData.getValue();
            String carInfo = deal.getCar().getBrand() + " " + deal.getCar().getModel();
            return new SimpleStringProperty(carInfo);
        });
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dealDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Настройка колонки с действиями
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Редактировать");
            private final Button deleteButton = new Button("Удалить");
            private final HBox buttons = new HBox(5, editButton, deleteButton);
            
            {
                buttons.setAlignment(Pos.CENTER);
                editButton.setOnAction(e -> {
                    Deal deal = getTableRow().getItem();
                    if (deal != null) {
                        handleEditDeal(deal);
                    }
                });
                deleteButton.setOnAction(e -> {
                    Deal deal = getTableRow().getItem();
                    if (deal != null) {
                        handleDeleteDeal(deal);
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });
        
        // Инициализация ComboBox статусов
        searchStatusComboBox.setItems(FXCollections.observableArrayList(
            "Новая",
            "В процессе",
            "Завершена",
            "Отменена"
        ));
        
        // Загрузка данных
        refreshDeals();
    }
    
    @FXML
    private void handleSearch() {
        String customerName = searchCustomerField.getText().trim();
        String carInfo = searchCarField.getText().trim();
        String status = searchStatusComboBox.getValue();
        
        List<Deal> deals = dealService.searchDeals(customerName, carInfo, status);
        dealsTable.setItems(FXCollections.observableArrayList(deals));
    }
    
    @FXML
    private void handleClearSearch() {
        searchCustomerField.clear();
        searchCarField.clear();
        searchStatusComboBox.setValue(null);
        refreshDeals();
    }
    
    @FXML
    private void handleAddDeal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/deal-form.fxml"));
            Parent page = loader.load();
            
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Новая сделка");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(dealsTable.getScene().getWindow());
            
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);
            
            DealController controller = loader.getController();
            
            dialogStage.showAndWait();
            
            // Обновляем таблицу после закрытия диалога
            refreshDeals();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Ошибка при открытии формы создания сделки");
        }
    }
    
    private void handleEditDeal(Deal deal) {
        boolean okClicked = showDealDialog(deal);
        if (okClicked) {
            dealService.updateDeal(deal);
            refreshDeals();
        }
    }
    
    private boolean showDealDialog(Deal deal) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/fxml/deal-dialog.fxml"));
            DialogPane dialogPane = loader.load();
            
            DealDialogController controller = loader.getController();
            controller.setDeal(deal);
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Редактирование сделки");
            
            Optional<ButtonType> result = dialog.showAndWait();
            return result.isPresent() && result.get() == ButtonType.OK;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private void handleDeleteDeal(Deal deal) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление сделки");
        alert.setContentText("Вы уверены, что хотите удалить эту сделку?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                dealService.deleteDeal(deal.getId());
                refreshDeals();
            }
        });
    }
    
    private void refreshDeals() {
        List<Deal> deals = dealService.getAllDeals();
        dealsTable.setItems(FXCollections.observableArrayList(deals));
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Обработчик кнопки "Создать сделку" из deals.fxml
     */
    @FXML
    private void handleCreateDeal() {
        handleAddDeal();
    }
} 