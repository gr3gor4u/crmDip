package com.example.javacrm.service;

public class ServiceInitializer {
    private static ServiceInitializer instance;
    private final DatabaseService databaseService;
    private final CustomerService customerService;
    private final CarService carService;
    private final InsuranceService insuranceService;
    private final DealService dealService;
    private final EquipmentService equipmentService;
    private final UserService userService;

    private ServiceInitializer() {
        databaseService = DatabaseService.getInstance();
        customerService = new CustomerService(databaseService);
        carService = new CarService(databaseService);
        insuranceService = new InsuranceService(databaseService, customerService, carService);
        equipmentService = new EquipmentService(databaseService);
        dealService = new DealService(databaseService, customerService, carService, insuranceService, equipmentService);
        userService = new UserService(databaseService);
    }

    public static ServiceInitializer getInstance() {
        if (instance == null) {
            instance = new ServiceInitializer();
        }
        return instance;
    }

    public DatabaseService getDatabaseService() {
        return databaseService;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public CarService getCarService() {
        return carService;
    }

    public InsuranceService getInsuranceService() {
        return insuranceService;
    }

    public DealService getDealService() {
        return dealService;
    }

    public EquipmentService getEquipmentService() {
        return equipmentService;
    }

    public UserService getUserService() {
        return userService;
    }
} 