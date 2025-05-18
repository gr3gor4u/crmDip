package com.example.javacrm.service;

import com.example.javacrm.model.Insurance;
import com.example.javacrm.model.Customer;
import com.example.javacrm.model.Car;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InsuranceService {
    private final DatabaseService databaseService;
    private final CustomerService customerService;
    private final CarService carService;

    public InsuranceService(DatabaseService databaseService, CustomerService customerService, CarService carService) {
        this.databaseService = databaseService;
        this.customerService = customerService;
        this.carService = carService;
    }

    public List<Insurance> getAllInsurances() {
        List<Insurance> insurances = new ArrayList<>();
        String query = "SELECT * FROM insurance";
        
        try (Connection conn = databaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Insurance insurance = new Insurance(
                    rs.getLong("id"),
                    rs.getString("car_vin"),
                    rs.getLong("customer_id"),
                    rs.getString("insurance_type"),
                    rs.getString("insurance_number"),
                    rs.getDouble("price"),
                    rs.getString("status")
                );
                insurance.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                
                // Load relationships
                Customer customer = customerService.getCustomerById(insurance.getCustomerId());
                if (customer != null) {
                    insurance.setCustomer(customer);
                }
                
                Car car = carService.getCarByVin(insurance.getCarVin());
                if (car != null) {
                    insurance.setCar(car);
                }
                
                insurances.add(insurance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insurances;
    }

    public Insurance getInsuranceById(Long id) {
        String query = "SELECT * FROM insurance WHERE id = ?";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Insurance insurance = new Insurance(
                    rs.getLong("id"),
                    rs.getString("car_vin"),
                    rs.getLong("customer_id"),
                    rs.getString("insurance_type"),
                    rs.getString("insurance_number"),
                    rs.getDouble("price"),
                    rs.getString("status")
                );
                insurance.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                
                // Load relationships
                Customer customer = customerService.getCustomerById(insurance.getCustomerId());
                if (customer != null) {
                    insurance.setCustomer(customer);
                }
                
                Car car = carService.getCarByVin(insurance.getCarVin());
                if (car != null) {
                    insurance.setCar(car);
                }
                
                return insurance;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addInsurance(Insurance insurance) {
        String query = "INSERT INTO insurance (car_vin, customer_id, insurance_type, insurance_number, price, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, insurance.getCarVin());
            pstmt.setLong(2, insurance.getCustomerId());
            pstmt.setString(3, insurance.getInsuranceType());
            pstmt.setString(4, insurance.getInsuranceNumber());
            pstmt.setDouble(5, insurance.getPrice());
            pstmt.setString(6, insurance.getStatus());
            pstmt.setTimestamp(7, Timestamp.valueOf(insurance.getCreatedAt()));
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    insurance.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateInsurance(Insurance insurance) {
        String query = "UPDATE insurance SET car_vin = ?, customer_id = ?, insurance_type = ?, insurance_number = ?, price = ?, status = ? WHERE id = ?";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, insurance.getCarVin());
            pstmt.setLong(2, insurance.getCustomerId());
            pstmt.setString(3, insurance.getInsuranceType());
            pstmt.setString(4, insurance.getInsuranceNumber());
            pstmt.setDouble(5, insurance.getPrice());
            pstmt.setString(6, insurance.getStatus());
            pstmt.setLong(7, insurance.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteInsurance(Long id) {
        String query = "DELETE FROM insurance WHERE id = ?";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Insurance> searchInsurances(String carVin, String customerId, String insuranceType) {
        List<Insurance> insurances = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM insurance WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (carVin != null && !carVin.isEmpty()) {
            query.append(" AND car_vin LIKE ?");
            params.add("%" + carVin + "%");
        }
        
        if (customerId != null && !customerId.isEmpty()) {
            query.append(" AND customer_id = ?");
            params.add(Long.parseLong(customerId));
        }
        
        if (insuranceType != null && !insuranceType.isEmpty()) {
            query.append(" AND insurance_type LIKE ?");
            params.add("%" + insuranceType + "%");
        }
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Insurance insurance = new Insurance(
                    rs.getLong("id"),
                    rs.getString("car_vin"),
                    rs.getLong("customer_id"),
                    rs.getString("insurance_type"),
                    rs.getString("insurance_number"),
                    rs.getDouble("price"),
                    rs.getString("status")
                );
                insurance.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                
                // Load relationships
                Customer customer = customerService.getCustomerById(insurance.getCustomerId());
                if (customer != null) {
                    insurance.setCustomer(customer);
                }
                
                Car car = carService.getCarByVin(insurance.getCarVin());
                if (car != null) {
                    insurance.setCar(car);
                }
                
                insurances.add(insurance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insurances;
    }
} 