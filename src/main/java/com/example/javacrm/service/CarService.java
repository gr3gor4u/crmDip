package com.example.javacrm.service;

import com.example.javacrm.model.Car;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CarService {
    private final DatabaseService databaseService;
    
    public CarService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }
    
    public List<Car> getAllCars() {
        return databaseService.getAllCars();
    }
    
    public Car getCarById(Long id) {
        return databaseService.getCarById(id);
    }
    
    public Car getCarByVin(String vin) {
        try (Connection conn = databaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM cars WHERE vin = ?")) {
            
            stmt.setString(1, vin);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Car car = new Car();
                car.setId(rs.getLong("id"));
                car.setVin(rs.getString("vin"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setYear(rs.getInt("year"));
                car.setPrice(rs.getBigDecimal("price"));
                car.setColor(rs.getString("color"));
                car.setKuzov(rs.getString("kuzov"));
                car.setEngineVolume(rs.getDouble("obem_dvig"));
                car.setHorsePower(rs.getInt("horse_power"));
                car.setStatus(rs.getString("status"));
                return car;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске автомобиля по VIN", e);
        }
        return null;
    }
    
    public void addCar(Car car) {
        String sql = "INSERT INTO cars (vin, brand, model, car_year, price, color, kuzov, obem_dvig, horse_power, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, car.getVin());
            pstmt.setString(2, car.getBrand());
            pstmt.setString(3, car.getModel());
            pstmt.setInt(4, car.getYear());
            pstmt.setDouble(5, car.getPrice().doubleValue());
            pstmt.setString(6, car.getColor());
            pstmt.setString(7, car.getKuzov());
            pstmt.setDouble(8, car.getEngineVolume().doubleValue());
            pstmt.setInt(9, car.getHorsePower());
            pstmt.setString(10, car.getStatus());
            pstmt.setTimestamp(11, Timestamp.valueOf(car.getCreatedAt()));
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                car.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateCar(Car car) {
        databaseService.updateCar(car);
    }
    
    public void deleteCar(Long id) {
        databaseService.deleteCar(id);
    }
    
    public List<Car> searchCars(String vin, String brand, String model, String color, String status) {
        return databaseService.searchCars(vin, brand, model, color, status);
    }
    
    public ObservableList<String> getAllCarVins() {
        List<String> vins = new ArrayList<>();
        String query = "SELECT vin FROM cars WHERE status = 'available'";
        
        try (Connection conn = databaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                vins.add(rs.getString("vin"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(vins);
    }
} 