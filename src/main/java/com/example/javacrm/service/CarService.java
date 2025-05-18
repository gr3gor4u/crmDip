package com.example.javacrm.service;

import com.example.javacrm.model.Car;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarService {
    private final DatabaseService databaseService;

    public CarService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        try (Connection conn = databaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM cars ORDER BY created_at DESC")) {
            
            while (rs.next()) {
                Car car = new Car();
                car.setId(rs.getLong("id"));
                car.setModel(rs.getString("model"));
                car.setBrand(rs.getString("brand"));
                car.setCarYear(rs.getInt("car_year"));
                car.setPrice(rs.getDouble("price"));
                car.setStatus(rs.getString("status"));
                car.setCreatedAt(rs.getString("created_at"));
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get cars", e);
        }
        return cars;
    }

    public void addCar(Car car) {
        try (Connection conn = databaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO cars (model, brand, car_year, price, status) VALUES (?, ?, ?, ?, ?)")) {
            
            stmt.setString(1, car.getModel());
            stmt.setString(2, car.getBrand());
            stmt.setInt(3, car.getCarYear());
            stmt.setDouble(4, car.getPrice());
            stmt.setString(5, car.getStatus());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add car", e);
        }
    }

    public void updateCar(Car car) {
        try (Connection conn = databaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE cars SET model = ?, brand = ?, car_year = ?, price = ?, status = ? WHERE id = ?")) {
            
            stmt.setString(1, car.getModel());
            stmt.setString(2, car.getBrand());
            stmt.setInt(3, car.getCarYear());
            stmt.setDouble(4, car.getPrice());
            stmt.setString(5, car.getStatus());
            stmt.setLong(6, car.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update car", e);
        }
    }

    public void deleteCar(Long id) {
        try (Connection conn = databaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM cars WHERE id = ?")) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete car", e);
        }
    }

    public void updateCarStatus(Long id, String status) {
        try (Connection conn = databaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE cars SET status = ? WHERE id = ?")) {
            
            stmt.setString(1, status);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update car status", e);
        }
    }
} 