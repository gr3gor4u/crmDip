package com.example.javacrm.service;

import com.example.javacrm.model.TradeInCar;
import com.example.javacrm.repository.TradeInCarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TradeInCarService {
    private final TradeInCarRepository repository;

    @Autowired
    public TradeInCarService(TradeInCarRepository repository) {
        this.repository = repository;
    }

    public List<TradeInCar> findAll() {
        return repository.findAll();
    }

    public Optional<TradeInCar> findById(String vinNumber) {
        return repository.findById(vinNumber);
    }

    public TradeInCar save(TradeInCar tradeInCar) {
        return repository.save(tradeInCar);
    }

    public void deleteById(String vinNumber) {
        repository.deleteById(vinNumber);
    }

    public List<TradeInCar> searchTradeInCars(String searchQuery, String brand, String condition) {
        List<TradeInCar> allCars = repository.findAll();
        
        return allCars.stream()
            .filter(car -> {
                boolean matchesSearch = searchQuery == null || searchQuery.isEmpty() ||
                    car.getVinNumber().toLowerCase().contains(searchQuery.toLowerCase()) ||
                    car.getBrand().toLowerCase().contains(searchQuery.toLowerCase()) ||
                    car.getModel().toLowerCase().contains(searchQuery.toLowerCase());
                
                boolean matchesBrand = brand == null || brand.isEmpty() ||
                    car.getBrand().equals(brand);
                
                boolean matchesCondition = condition == null || condition.isEmpty() ||
                    car.getCondition().equals(condition);
                
                return matchesSearch && matchesBrand && matchesCondition;
            })
            .collect(Collectors.toList());
    }
} 

import com.example.javacrm.model.TradeInCar;
import com.example.javacrm.repository.TradeInCarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TradeInCarService {
    private final TradeInCarRepository repository;

    @Autowired
    public TradeInCarService(TradeInCarRepository repository) {
        this.repository = repository;
    }

    public List<TradeInCar> findAll() {
        return repository.findAll();
    }

    public Optional<TradeInCar> findById(String vinNumber) {
        return repository.findById(vinNumber);
    }

    public TradeInCar save(TradeInCar tradeInCar) {
        return repository.save(tradeInCar);
    }

    public void deleteById(String vinNumber) {
        repository.deleteById(vinNumber);
    }

    public List<TradeInCar> searchTradeInCars(String searchQuery, String brand, String condition) {
        List<TradeInCar> allCars = repository.findAll();
        
        return allCars.stream()
            .filter(car -> {
                boolean matchesSearch = searchQuery == null || searchQuery.isEmpty() ||
                    car.getVinNumber().toLowerCase().contains(searchQuery.toLowerCase()) ||
                    car.getBrand().toLowerCase().contains(searchQuery.toLowerCase()) ||
                    car.getModel().toLowerCase().contains(searchQuery.toLowerCase());
                
                boolean matchesBrand = brand == null || brand.isEmpty() ||
                    car.getBrand().equals(brand);
                
                boolean matchesCondition = condition == null || condition.isEmpty() ||
                    car.getCondition().equals(condition);
                
                return matchesSearch && matchesBrand && matchesCondition;
            })
            .collect(Collectors.toList());
    }
} 