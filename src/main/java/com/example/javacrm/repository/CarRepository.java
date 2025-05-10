package com.example.javacrm.repository;

import com.example.javacrm.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByBrandAndModel(String brand, String model);
    List<Car> findByStatus(Car.CarStatus status);
    Car findByVin(String vin);
} 