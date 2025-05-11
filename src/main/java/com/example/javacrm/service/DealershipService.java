package com.example.javacrm.service;

import com.example.javacrm.model.Car;
import com.example.javacrm.model.CarStatus;
import com.example.javacrm.model.Customer;
import com.example.javacrm.model.Deal;
import com.example.javacrm.model.DealStatus;
import com.example.javacrm.repository.CarDao;
import com.example.javacrm.repository.CustomerDao;
import com.example.javacrm.repository.DealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DealershipService {
    private final CarDao carDao;
    private final CustomerDao customerDao;
    private final DealRepository dealRepository;

    @Autowired
    public DealershipService(CarDao carDao,
                           CustomerDao customerDao,
                           DealRepository dealRepository) {
        this.carDao = carDao;
        this.customerDao = customerDao;
        this.dealRepository = dealRepository;
    }

    // Car operations
    public Car addCar(Car car) {
        return carDao.save(car);
    }

    public List<Car> getAllCars() {
        return carDao.findAll();
    }

    public Car getCarByVin(String vinNumber) {
        return carDao.findByVinNumber(vinNumber).orElse(null);
    }

    public List<Car> getCarsByStatus(CarStatus status) {
        return carDao.findAll().stream()
                .filter(car -> car.getStatus() == status)
                .collect(Collectors.toList());
    }

    public List<Car> getAvailableCars() {
        return getCarsByStatus(CarStatus.AVAILABLE);
    }

    public Optional<Car> getCarById(Long id) {
        return carDao.findById(id);
    }

    // Customer operations
    public Customer addCustomer(Customer customer) {
        customer.setCreatedAt(LocalDateTime.now());
        return customerDao.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerDao.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
    }

    public Customer updateCustomer(Customer customer) {
        Customer existingCustomer = getCustomerById(customer.getId());
        existingCustomer.setFullName(customer.getFullName());
        existingCustomer.setBirthPlace(customer.getBirthPlace());
        existingCustomer.setPassportData(customer.getPassportData());
        existingCustomer.setEmail(customer.getEmail());
        existingCustomer.setPhone(customer.getPhone());
        existingCustomer.setTags(customer.getTags());
        existingCustomer.setNotes(customer.getNotes());
        return customerDao.save(existingCustomer);
    }

    public void deleteCustomer(Long id) {
        customerDao.findById(id).ifPresent(customerDao::delete);
    }

    public List<Customer> searchCustomers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCustomers();
        }
        final String searchQuery = query.toLowerCase();
        return customerDao.findAll().stream()
                .filter(customer -> 
                    customer.getFullName().toLowerCase().contains(searchQuery) ||
                    (customer.getEmail() != null && customer.getEmail().toLowerCase().contains(searchQuery)) ||
                    (customer.getPhone() != null && customer.getPhone().contains(searchQuery)))
                .collect(Collectors.toList());
    }

    public List<Customer> searchCustomersByTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return getAllCustomers();
        }
        return customerDao.findAll().stream()
                .filter(customer -> customer.getTags().contains(tag))
                .collect(Collectors.toList());
    }

    // Deal operations
    @Transactional
    public Deal createDeal(Long customerId, String carVin, BigDecimal amount) {
        Customer customer = customerDao.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Car car = carDao.findByVinNumber(carVin)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        Deal deal = new Deal();
        deal.setCustomer(customer);
        deal.setCar(car);
        deal.setAmount(amount);
        deal.setStatus(DealStatus.NEW);
        deal.setDate(LocalDateTime.now());

        return dealRepository.save(deal);
    }

    @Transactional
    public Deal scheduleTestDrive(Long dealId, LocalDateTime testDriveDate) {
        Deal deal = dealRepository.findById(dealId)
                .orElseThrow(() -> new RuntimeException("Deal not found"));
        deal.setTestDriveDate(testDriveDate);
        deal.setStatus(DealStatus.IN_PROGRESS);
        return dealRepository.save(deal);
    }

    @Transactional
    public Deal completeDeal(Long dealId) {
        Deal deal = dealRepository.findById(dealId)
                .orElseThrow(() -> new RuntimeException("Deal not found"));
        deal.setStatus(DealStatus.COMPLETED);
        deal.setDate(LocalDateTime.now());

        Car car = deal.getCar();
        car.setStatus(CarStatus.SOLD);
        carDao.save(car);

        return dealRepository.save(deal);
    }

    // Statistics
    public BigDecimal getTotalSales() {
        return dealRepository.findAll().stream()
                .filter(deal -> deal.getStatus() == DealStatus.COMPLETED)
                .map(Deal::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long getCarsSold() {
        return carDao.findAll().stream()
                .filter(car -> car.getStatus() == CarStatus.SOLD)
                .count();
    }

    public long getAvailableCarsCount() {
        return carDao.findAll().stream()
                .filter(car -> car.getStatus() == CarStatus.AVAILABLE)
                .count();
    }

    public long getPendingDeals() {
        return dealRepository.findByStatus(DealStatus.NEW).size() +
               dealRepository.findByStatus(DealStatus.IN_PROGRESS).size();
    }

    // Recent deals
    public List<Deal> getRecentDeals() {
        return dealRepository.findAll().stream()
                .sorted((d1, d2) -> d2.getCreatedAt().compareTo(d1.getCreatedAt()))
                .limit(10)
                .toList();
    }

    // --- ServiceRecord operations (заглушки) ---
    public java.util.List<com.example.javacrm.model.ServiceRecord> getAllServiceRecords() {
        return java.util.Collections.emptyList();
    }

    public java.util.List<com.example.javacrm.model.ServiceRecord> searchServiceRecords(String query, String status, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return java.util.Collections.emptyList();
    }

    public void addServiceRecord(com.example.javacrm.model.ServiceRecord record) {}

    public void updateServiceRecord(com.example.javacrm.model.ServiceRecord record) {}

    public void deleteServiceRecord(Long id) {}

    // --- Deal operations (заглушки) ---
    public java.util.List<com.example.javacrm.model.Deal> getAllDeals() {
        return java.util.Collections.emptyList();
    }

    public java.util.List<com.example.javacrm.model.Deal> searchDeals(String query, String status, java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return java.util.Collections.emptyList();
    }

    public void addDeal(com.example.javacrm.model.Deal deal) {}

    public void updateDeal(com.example.javacrm.model.Deal deal) {}

    public void deleteDeal(Long id) {}

    // --- Car operations (заглушки) ---
    public java.util.List<com.example.javacrm.model.Car> searchCars(String query, String brand, String status) {
        return java.util.Collections.emptyList();
    }

    public void updateCar(com.example.javacrm.model.Car car) {}

    public void deleteCar(String vinNumber) {}

    // --- Reports (заглушки) ---
    public java.math.BigDecimal getTotalSales(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return java.math.BigDecimal.ZERO;
    }

    public long getTotalDeals(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return 0L;
    }

    public java.math.BigDecimal getAverageDealAmount(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return java.math.BigDecimal.ZERO;
    }

    public java.util.Map<String, Long> getSalesByBrand(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return java.util.Collections.emptyMap();
    }

    public java.util.Map<String, Long> getSalesByMonth(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return java.util.Collections.emptyMap();
    }

    public java.util.Map<String, java.math.BigDecimal> getRevenueByMonth(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return java.util.Collections.emptyMap();
    }

    public java.util.List<java.util.Map.Entry<String, java.math.BigDecimal>> getTopCustomers(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return java.util.Collections.emptyList();
    }

    public java.util.List<java.util.Map.Entry<String, java.math.BigDecimal>> getTopCars(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return java.util.Collections.emptyList();
    }
} 
        return java.util.Collections.emptyMap();
    }

    public java.util.List<java.util.Map.Entry<String, java.math.BigDecimal>> getTopCustomers(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return java.util.Collections.emptyList();
    }

    public java.util.List<java.util.Map.Entry<String, java.math.BigDecimal>> getTopCars(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        return java.util.Collections.emptyList();
    }
} 