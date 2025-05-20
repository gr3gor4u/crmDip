package com.example.javacrm.service;

import com.example.javacrm.model.Car;
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
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars ORDER BY brand, model";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Car car = new Car();
                car.setId(rs.getLong("id"));
                car.setVin(rs.getString("vin"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setYear(rs.getInt("car_year"));
                car.setPrice(rs.getDouble("price"));
                car.setColor(rs.getString("color"));
                car.setKuzov(rs.getString("kuzov"));
                car.setEngineVolume(rs.getDouble("obem_dvig"));
                car.setHorsePower(rs.getInt("horse_power"));
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
        String sql = "INSERT INTO cars (vin, brand, model, car_year, price, color, kuzov, obem_dvig, horse_power, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, car.getVin());
            pstmt.setString(2, car.getBrand());
            pstmt.setString(3, car.getModel());
            pstmt.setInt(4, car.getYear());
            pstmt.setDouble(5, car.getPrice());
            pstmt.setString(6, car.getColor());
            pstmt.setString(7, car.getKuzov());
            pstmt.setDouble(8, car.getEngineVolume());
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
        String sql = "UPDATE cars SET vin = ?, brand = ?, model = ?, car_year = ?, price = ?, " +
                    "color = ?, kuzov = ?, obem_dvig = ?, horse_power = ?, status = ? WHERE id = ?";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, car.getVin());
            pstmt.setString(2, car.getBrand());
            pstmt.setString(3, car.getModel());
            pstmt.setInt(4, car.getYear());
            pstmt.setDouble(5, car.getPrice());
            pstmt.setString(6, car.getColor());
            pstmt.setString(7, car.getKuzov());
            pstmt.setDouble(8, car.getEngineVolume());
            pstmt.setInt(9, car.getHorsePower());
            pstmt.setString(10, car.getStatus());
            pstmt.setLong(11, car.getId());
            
            pstmt.executeUpdate();
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
            sql.append(" AND vin ILIKE ?");
            params.add("%" + vin + "%");
        }
        if (brand != null && !brand.isEmpty()) {
            sql.append(" AND brand ILIKE ?");
            params.add("%" + brand + "%");
        }
        if (model != null && !model.isEmpty()) {
            sql.append(" AND model ILIKE ?");
            params.add("%" + model + "%");
        }
        if (color != null && !color.isEmpty()) {
            sql.append(" AND color ILIKE ?");
            params.add("%" + color + "%");
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        
        sql.append(" ORDER BY brand, model");

        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Car car = new Car();
                car.setId(rs.getLong("id"));
                car.setVin(rs.getString("vin"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setYear(rs.getInt("car_year"));
                car.setPrice(rs.getDouble("price"));
                car.setColor(rs.getString("color"));
                car.setKuzov(rs.getString("kuzov"));
                car.setEngineVolume(rs.getDouble("obem_dvig"));
                car.setHorsePower(rs.getInt("horse_power"));
                car.setStatus(rs.getString("status"));
                car.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                cars.add(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return cars;
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