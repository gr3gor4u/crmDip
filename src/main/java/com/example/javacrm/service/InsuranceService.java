package com.example.javacrm.service;

import com.example.javacrm.model.Insurance;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;
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

    public ObservableList<Insurance> getAllInsurances() {
        ObservableList<Insurance> insurances = FXCollections.observableArrayList();
        String query = "SELECT i.*, c.first_name, c.last_name, car.brand, car.model " +
                      "FROM insurance i " +
                      "LEFT JOIN customers c ON i.customer_id = c.id " +
                      "LEFT JOIN cars car ON i.car_vin = car.vin " +
                      "ORDER BY i.start_date DESC";

        try (Connection conn = databaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Insurance insurance = new Insurance();
                insurance.setId(rs.getLong("id"));
                insurance.setCustomerId(rs.getLong("customer_id"));
                insurance.setCarVin(rs.getString("car_vin"));
                insurance.setInsuranceType(Insurance.InsuranceType.valueOf(rs.getString("insurance_type")));
                insurance.setInsuranceNumber(rs.getString("insurance_number"));
                insurance.setStartDate(rs.getDate("start_date").toLocalDate());
                insurance.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
                insurance.setStatus(rs.getString("status"));
                
                // Устанавливаем дополнительные поля
                insurance.setCustomerFirstName(rs.getString("first_name"));
                insurance.setCustomerLastName(rs.getString("last_name"));
                insurance.setCarBrand(rs.getString("brand"));
                insurance.setCarModel(rs.getString("model"));
                
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
                Insurance insurance = new Insurance();
                insurance.setId(rs.getLong("id"));
                insurance.setCustomerId(rs.getLong("customer_id"));
                insurance.setCarVin(rs.getString("car_vin"));
                insurance.setInsuranceType(Insurance.InsuranceType.valueOf(rs.getString("insurance_type")));
                insurance.setInsuranceNumber(rs.getString("insurance_number"));
                insurance.setStartDate(rs.getDate("start_date").toLocalDate());
                insurance.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
                insurance.setStatus(rs.getString("status"));
                return insurance;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addInsurance(Insurance insurance) {
        String sql = "INSERT INTO insurance (customer_id, car_vin, insurance_type, start_date, expiry_date, status) " +
                "VALUES (?, ?, ?::insurance_type, ?, ?, ?::insurance_status)";
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, insurance.getCustomerId());
            pstmt.setString(2, insurance.getCarVin());
            pstmt.setString(3, insurance.getInsuranceType().toString());
            pstmt.setDate(4, java.sql.Date.valueOf(insurance.getStartDate()));
            pstmt.setDate(5, java.sql.Date.valueOf(insurance.getExpiryDate()));
            pstmt.setString(6, insurance.getStatus().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateInsurance(Insurance insurance) {
        String query = "UPDATE insurance SET customer_id = ?, car_vin = ?, insurance_type = ?::insurance_type, " +
                      "start_date = ?, expiry_date = ?, status = ?::insurance_status WHERE id = ?";

        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setLong(1, insurance.getCustomerId());
            pstmt.setString(2, insurance.getCarVin());
            pstmt.setString(3, insurance.getInsuranceType().name());
            pstmt.setDate(4, Date.valueOf(insurance.getStartDate()));
            pstmt.setDate(5, Date.valueOf(insurance.getExpiryDate()));
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

    public ObservableList<Insurance> searchInsurances(String number, String vin, String customer, 
            Insurance.InsuranceType type, String status) {
        ObservableList<Insurance> insurances = FXCollections.observableArrayList();
        StringBuilder query = new StringBuilder(
            "SELECT i.*, c.first_name, c.last_name, car.brand, car.model " +
            "FROM insurance i " +
            "LEFT JOIN customers c ON i.customer_id = c.id " +
            "LEFT JOIN cars car ON i.car_vin = car.vin " +
            "WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (number != null && !number.isEmpty()) {
            query.append(" AND i.insurance_number LIKE ?");
            params.add("%" + number + "%");
        }

        if (vin != null && !vin.isEmpty()) {
            query.append(" AND i.car_vin LIKE ?");
            params.add("%" + vin + "%");
        }

        if (customer != null && !customer.isEmpty()) {
            query.append(" AND (c.first_name LIKE ? OR c.last_name LIKE ?)");
            params.add("%" + customer + "%");
            params.add("%" + customer + "%");
        }

        if (type != null) {
            query.append(" AND i.insurance_type = ?::insurance_type");
            params.add(type.toString());
        }

        if (status != null && !status.equals("Все")) {
            query.append(" AND i.status = ?::insurance_status");
            params.add(status);
        }

        query.append(" ORDER BY i.start_date DESC");

        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Insurance insurance = new Insurance();
                    insurance.setId(rs.getLong("id"));
                    insurance.setCustomerId(rs.getLong("customer_id"));
                    insurance.setCarVin(rs.getString("car_vin"));
                    insurance.setInsuranceType(Insurance.InsuranceType.valueOf(rs.getString("insurance_type")));
                    insurance.setInsuranceNumber(rs.getString("insurance_number"));
                    insurance.setStartDate(rs.getDate("start_date").toLocalDate());
                    insurance.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
                    insurance.setStatus(rs.getString("status"));
                    insurance.setCustomerFirstName(rs.getString("first_name"));
                    insurance.setCustomerLastName(rs.getString("last_name"));
                    insurance.setCarBrand(rs.getString("brand"));
                    insurance.setCarModel(rs.getString("model"));
                    insurances.add(insurance);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insurances;
    }

    public void updateInsuranceStatus() {
        String query = "UPDATE insurance SET status = 'Истекла' " +
                      "WHERE expiry_date < CURRENT_DATE AND status = 'Активна'";

        try (Connection conn = databaseService.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 