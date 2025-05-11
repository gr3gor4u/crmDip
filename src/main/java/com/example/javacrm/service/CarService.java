package com.example.javacrm.service;

import com.example.javacrm.model.Car;
import com.example.javacrm.model.CarStatus;
import com.example.javacrm.repository.CarDao;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarService {

    private final CarDao carDao;

    @Autowired
    public CarService(CarDao carDao) {
        this.carDao = carDao;
    }

    public List<Car> getAllCars() {
        return carDao.findAll();
    }

    public Car getCarById(Long id) {
        return carDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден"));
    }

    public Car getCarByVin(String vinNumber) {
        return carDao.findByVinNumber(vinNumber)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден"));
    }

    public Car createCar(Car car) {
        if (carDao.existsByVinNumber(car.getVinNumber())) {
            throw new IllegalArgumentException("Автомобиль с таким VIN уже существует");
        }
        carDao.save(car);
        return car;
    }

    public Car updateCar(Long id, Car car) {
        Car existingCar = getCarById(id);
        if (!existingCar.getVinNumber().equals(car.getVinNumber()) &&
            carDao.existsByVinNumber(car.getVinNumber())) {
            throw new IllegalArgumentException("Автомобиль с таким VIN уже существует");
        }
        // Обновляем все поля
        existingCar.setVinNumber(car.getVinNumber());
        existingCar.setBrand(car.getBrand());
        existingCar.setModel(car.getModel());
        existingCar.setYear(car.getYear());
        existingCar.setColor(car.getColor());
        existingCar.setPrice(car.getPrice());
        existingCar.setBodyType(car.getBodyType());
        existingCar.setEngineVolume(car.getEngineVolume());
        existingCar.setHorsePower(car.getHorsePower());
        existingCar.setMileage(car.getMileage());
        existingCar.setStatus(car.getStatus());
        existingCar.setDescription(car.getDescription());
        carDao.update(existingCar);
        return existingCar;
    }

    public void deleteCar(Long id) {
        Car car = getCarById(id);
        if (car.getStatus() == CarStatus.SOLD) {
            throw new IllegalStateException("Нельзя удалить проданный автомобиль");
        }
        carDao.delete(car);
    }

    public List<Car> searchCars(String searchText, String brand, CarStatus status) {
        List<Car> cars = carDao.findAll();
        return cars.stream()
                .filter(car -> {
                    boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                            car.getVinNumber().toLowerCase().contains(searchText.toLowerCase()) ||
                            car.getBrand().toLowerCase().contains(searchText.toLowerCase()) ||
                            car.getModel().toLowerCase().contains(searchText.toLowerCase());
                    boolean matchesBrand = brand == null || brand.isEmpty() ||
                            car.getBrand().equals(brand);
                    boolean matchesStatus = status == null ||
                            car.getStatus() == status;
                    return matchesSearch && matchesBrand && matchesStatus;
                })
                .collect(Collectors.toList());
    }

    public List<String> getAllBrands() {
        return carDao.getAllBrands();
    }
} 

import com.example.javacrm.model.Car;
import com.example.javacrm.model.CarStatus;
import com.example.javacrm.repository.CarDao;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarService {

    private final CarDao carDao;

    @Autowired
    public CarService(CarDao carDao) {
        this.carDao = carDao;
    }

    public List<Car> getAllCars() {
        return carDao.findAll();
    }

    public Car getCarById(Long id) {
        return carDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден"));
    }

    public Car getCarByVin(String vinNumber) {
        return carDao.findByVinNumber(vinNumber)
                .orElseThrow(() -> new RuntimeException("Автомобиль не найден"));
    }

    public Car createCar(Car car) {
        if (carDao.existsByVinNumber(car.getVinNumber())) {
            throw new IllegalArgumentException("Автомобиль с таким VIN уже существует");
        }
        carDao.save(car);
        return car;
    }

    public Car updateCar(Long id, Car car) {
        Car existingCar = getCarById(id);
        if (!existingCar.getVinNumber().equals(car.getVinNumber()) &&
            carDao.existsByVinNumber(car.getVinNumber())) {
            throw new IllegalArgumentException("Автомобиль с таким VIN уже существует");
        }
        // Обновляем все поля
        existingCar.setVinNumber(car.getVinNumber());
        existingCar.setBrand(car.getBrand());
        existingCar.setModel(car.getModel());
        existingCar.setYear(car.getYear());
        existingCar.setColor(car.getColor());
        existingCar.setPrice(car.getPrice());
        existingCar.setBodyType(car.getBodyType());
        existingCar.setEngineVolume(car.getEngineVolume());
        existingCar.setHorsePower(car.getHorsePower());
        existingCar.setMileage(car.getMileage());
        existingCar.setStatus(car.getStatus());
        existingCar.setDescription(car.getDescription());
        carDao.update(existingCar);
        return existingCar;
    }

    public void deleteCar(Long id) {
        Car car = getCarById(id);
        if (car.getStatus() == CarStatus.SOLD) {
            throw new IllegalStateException("Нельзя удалить проданный автомобиль");
        }
        carDao.delete(car);
    }

    public List<Car> searchCars(String searchText, String brand, CarStatus status) {
        List<Car> cars = carDao.findAll();
        return cars.stream()
                .filter(car -> {
                    boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                            car.getVinNumber().toLowerCase().contains(searchText.toLowerCase()) ||
                            car.getBrand().toLowerCase().contains(searchText.toLowerCase()) ||
                            car.getModel().toLowerCase().contains(searchText.toLowerCase());
                    boolean matchesBrand = brand == null || brand.isEmpty() ||
                            car.getBrand().equals(brand);
                    boolean matchesStatus = status == null ||
                            car.getStatus() == status;
                    return matchesSearch && matchesBrand && matchesStatus;
                })
                .collect(Collectors.toList());
    }

    public List<String> getAllBrands() {
        return carDao.getAllBrands();
    }
} 