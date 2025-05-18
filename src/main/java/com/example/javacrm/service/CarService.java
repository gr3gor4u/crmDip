package com.example.javacrm.service;

import com.example.javacrm.model.Car;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CarService {
    private final DatabaseService databaseService;
    
    public CarService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }
    
    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Car car = new Car();
                car.setId(rs.getLong("id"));
                car.setVin(rs.getString("vin"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setYear(rs.getInt("year"));
                car.setColor(rs.getString("color"));
                car.setStatus(rs.getString("status"));
                car.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                cars.add(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return cars;
    }
    
    public Car getCarById(Long id) {
        String sql = "SELECT * FROM cars WHERE id = ?";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Car car = new Car();
                car.setId(rs.getLong("id"));
                car.setVin(rs.getString("vin"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setYear(rs.getInt("year"));
                car.setColor(rs.getString("color"));
                car.setStatus(rs.getString("status"));
                car.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return car;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public Car getCarByVin(String vin) {
        String sql = "SELECT * FROM cars WHERE vin = ?";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, vin);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Car car = new Car();
                car.setId(rs.getLong("id"));
                car.setVin(rs.getString("vin"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setYear(rs.getInt("year"));
                car.setColor(rs.getString("color"));
                car.setStatus(rs.getString("status"));
                car.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return car;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public void addCar(Car car) {
        String sql = "INSERT INTO cars (vin, brand, model, year, color, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, car.getVin());
            stmt.setString(2, car.getBrand());
            stmt.setString(3, car.getModel());
            stmt.setInt(4, car.getYear());
            stmt.setString(5, car.getColor());
            stmt.setString(6, car.getStatus());
            stmt.setTimestamp(7, Timestamp.valueOf(car.getCreatedAt()));
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                car.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateCar(Car car) {
        String sql = "UPDATE cars SET vin = ?, brand = ?, model = ?, year = ?, color = ?, status = ? WHERE id = ?";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, car.getVin());
            stmt.setString(2, car.getBrand());
            stmt.setString(3, car.getModel());
            stmt.setInt(4, car.getYear());
            stmt.setString(5, car.getColor());
            stmt.setString(6, car.getStatus());
            stmt.setLong(7, car.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteCar(Long id) {
        String sql = "DELETE FROM cars WHERE id = ?";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<Car> searchCars(String vin, String brand, String model, String color, String status) {
        List<Car> cars = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM cars WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (vin != null && !vin.isEmpty()) {
            sql.append(" AND vin LIKE ?");
            params.add("%" + vin + "%");
        }
        if (brand != null && !brand.isEmpty()) {
            sql.append(" AND brand LIKE ?");
            params.add("%" + brand + "%");
        }
        if (model != null && !model.isEmpty()) {
            sql.append(" AND model LIKE ?");
            params.add("%" + model + "%");
        }
        if (color != null && !color.isEmpty()) {
            sql.append(" AND color LIKE ?");
            params.add("%" + color + "%");
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status LIKE ?");
            params.add("%" + status + "%");
        }
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Car car = new Car();
                car.setId(rs.getLong("id"));
                car.setVin(rs.getString("vin"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setYear(rs.getInt("year"));
                car.setColor(rs.getString("color"));
                car.setStatus(rs.getString("status"));
                car.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                cars.add(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return cars;
    }
} 