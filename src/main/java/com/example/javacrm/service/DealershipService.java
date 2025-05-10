package com.example.javacrm.service;

import com.example.javacrm.model.Car;
import com.example.javacrm.model.Customer;
import com.example.javacrm.model.Deal;
import com.example.javacrm.repository.CarRepository;
import com.example.javacrm.repository.CustomerRepository;
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
    private final CarRepository carRepository;
    private final CustomerRepository customerRepository;
    private final DealRepository dealRepository;

    @Autowired
    public DealershipService(CarRepository carRepository,
                           CustomerRepository customerRepository,
                           DealRepository dealRepository) {
        this.carRepository = carRepository;
        this.customerRepository = customerRepository;
        this.dealRepository = dealRepository;
    }

    // Car operations
    public Car addCar(Car car) {
        return carRepository.save(car);
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Optional<Car> getCarById(Long id) {
        return carRepository.findById(id);
    }

    public List<Car> getCarsByStatus(Car.CarStatus status) {
        return carRepository.findByStatus(status);
    }

    // Customer operations
    public Customer addCustomer(Customer customer) {
        customer.setCreatedAt(LocalDateTime.now());
        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
    }

    public Customer updateCustomer(Customer customer) {
        Customer existingCustomer = getCustomerById(customer.getId());
        existingCustomer.setFirstName(customer.getFirstName());
        existingCustomer.setLastName(customer.getLastName());
        existingCustomer.setEmail(customer.getEmail());
        existingCustomer.setPhone(customer.getPhone());
        existingCustomer.setTags(customer.getTags());
        existingCustomer.setNotes(customer.getNotes());
        return customerRepository.save(existingCustomer);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public List<Customer> searchCustomers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllCustomers();
        }
        final String searchQuery = query.toLowerCase();
        return customerRepository.findAll().stream()
                .filter(customer -> 
                    customer.getFirstName().toLowerCase().contains(searchQuery) ||
                    customer.getLastName().toLowerCase().contains(searchQuery) ||
                    customer.getEmail().toLowerCase().contains(searchQuery) ||
                    customer.getPhone().contains(searchQuery))
                .collect(Collectors.toList());
    }

    public List<Customer> searchCustomersByTag(String tag) {
        if (tag == null || tag.trim().isEmpty()) {
            return getAllCustomers();
        }
        return customerRepository.findAll().stream()
                .filter(customer -> customer.getTags().contains(tag))
                .collect(Collectors.toList());
    }

    // Deal operations
    @Transactional
    public Deal createDeal(Long customerId, Long carId, BigDecimal amount) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new RuntimeException("Car not found"));

        Deal deal = new Deal();
        deal.setCustomer(customer);
        deal.setCar(car);
        deal.setAmount(amount);
        deal.setStatus(Deal.DealStatus.LEAD);

        return dealRepository.save(deal);
    }

    @Transactional
    public Deal scheduleTestDrive(Long dealId, LocalDateTime testDriveDate) {
        Deal deal = dealRepository.findById(dealId)
                .orElseThrow(() -> new RuntimeException("Deal not found"));
        deal.setTestDriveDate(testDriveDate);
        deal.setStatus(Deal.DealStatus.TEST_DRIVE_SCHEDULED);
        return dealRepository.save(deal);
    }

    @Transactional
    public Deal completeDeal(Long dealId) {
        Deal deal = dealRepository.findById(dealId)
                .orElseThrow(() -> new RuntimeException("Deal not found"));
        deal.setStatus(Deal.DealStatus.COMPLETED);
        deal.setDealDate(LocalDateTime.now());

        Car car = deal.getCar();
        car.setStatus(Car.CarStatus.SOLD);
        carRepository.save(car);

        return dealRepository.save(deal);
    }

    // Statistics
    public BigDecimal getTotalSales() {
        return dealRepository.findAll().stream()
                .filter(deal -> deal.getStatus() == Deal.DealStatus.COMPLETED)
                .map(Deal::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public long getCarsSold() {
        return carRepository.findByStatus(Car.CarStatus.SOLD).size();
    }

    public long getAvailableCars() {
        return carRepository.findByStatus(Car.CarStatus.NEW).size() +
               carRepository.findByStatus(Car.CarStatus.USED).size();
    }

    public long getPendingDeals() {
        return dealRepository.findByStatus(Deal.DealStatus.LEAD).size() +
               dealRepository.findByStatus(Deal.DealStatus.NEGOTIATION).size() +
               dealRepository.findByStatus(Deal.DealStatus.PENDING_APPROVAL).size();
    }

    // Recent deals
    public List<Deal> getRecentDeals() {
        return dealRepository.findAll().stream()
                .sorted((d1, d2) -> d2.getCreatedAt().compareTo(d1.getCreatedAt()))
                .limit(10)
                .toList();
    }
} 