package com.example.javacrm.dao;

import com.example.javacrm.model.Customer;
import com.example.javacrm.model.Car;
import com.example.javacrm.model.User;
import com.example.javacrm.model.AdditionalEquipment;
import com.example.javacrm.model.DealEquipment;
import com.example.javacrm.model.Deal;
import com.example.javacrm.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DealDAO {
    
    public Deal create(Deal deal) throws SQLException {
        String sql = "INSERT INTO deals (car_id, customer_id, manager_id, insurance_number, no_insurance, " +
                    "total_price, status, deal_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, deal.getCarId());
            stmt.setLong(2, deal.getCustomerId());
            stmt.setLong(3, deal.getManagerId());
            stmt.setString(4, deal.getInsuranceNumber());
            stmt.setBoolean(5, deal.isNoInsurance());
            stmt.setBigDecimal(6, deal.getTotalPrice());
            stmt.setString(7, deal.getStatus());
            stmt.setDate(8, Date.valueOf(deal.getDealDate()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating deal failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    deal.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating deal failed, no ID obtained.");
                }
            }
            
            // Сохраняем оборудование
            if (deal.getEquipment() != null) {
                for (DealEquipment equipment : deal.getEquipment()) {
                    equipment.setDealId(deal.getId());
                    saveDealEquipment(equipment);
                }
            }
            
            return deal;
        }
    }
    
    private void saveDealEquipment(DealEquipment equipment) throws SQLException {
        String sql = "INSERT INTO deal_equipment (deal_id, equipment_id, quantity, price, no_equipment) " +
                    "VALUES (?, ?, ?, ?, ?)";
                    
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, equipment.getDealId());
            stmt.setLong(2, equipment.getEquipmentId());
            stmt.setInt(3, equipment.getQuantity());
            stmt.setBigDecimal(4, equipment.getPrice());
            stmt.setBoolean(5, equipment.isNoEquipment());
            
            stmt.executeUpdate();
        }
    }
    
    public List<Deal> findAll() throws SQLException {
        String sql = "SELECT d.*, c.*, car.*, u.* FROM deals d " +
                    "JOIN customers c ON d.customer_id = c.id " +
                    "JOIN cars car ON d.car_id = car.id " +
                    "JOIN users u ON d.manager_id = u.id";
                    
        List<Deal> deals = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Deal deal = mapDealFromResultSet(rs);
                deal.setEquipment(findDealEquipment(deal.getId()));
                deals.add(deal);
            }
        }
        
        return deals;
    }
    
    private List<DealEquipment> findDealEquipment(Long dealId) throws SQLException {
        String sql = "SELECT de.*, ae.* FROM deal_equipment de " +
                    "JOIN additional_equipment ae ON de.equipment_id = ae.id " +
                    "WHERE de.deal_id = ?";
                    
        List<DealEquipment> equipment = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, dealId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    equipment.add(mapDealEquipmentFromResultSet(rs));
                }
            }
        }
        
        return equipment;
    }
    
    private Deal mapDealFromResultSet(ResultSet rs) throws SQLException {
        Deal deal = new Deal();
        deal.setId(rs.getLong("id"));
        deal.setCarId(rs.getLong("car_id"));
        deal.setCustomerId(rs.getLong("customer_id"));
        deal.setManagerId(rs.getLong("manager_id"));
        deal.setInsuranceNumber(rs.getString("insurance_number"));
        deal.setNoInsurance(rs.getBoolean("no_insurance"));
        deal.setTotalPrice(rs.getBigDecimal("total_price"));
        deal.setStatus(rs.getString("status"));
        deal.setDealDate(rs.getDate("deal_date").toLocalDate());
        
        // Map related objects
        deal.setCustomer(mapCustomerFromResultSet(rs));
        deal.setCar(mapCarFromResultSet(rs));
        deal.setManager(mapUserFromResultSet(rs));
        
        return deal;
    }
    
    private DealEquipment mapDealEquipmentFromResultSet(ResultSet rs) throws SQLException {
        DealEquipment equipment = new DealEquipment();
        equipment.setDealId(rs.getLong("deal_id"));
        equipment.setEquipmentId(rs.getLong("equipment_id"));
        equipment.setQuantity(rs.getInt("quantity"));
        equipment.setPrice(rs.getBigDecimal("price"));
        equipment.setNoEquipment(rs.getBoolean("no_equipment"));
        
        // Map related equipment
        equipment.setEquipment(mapAdditionalEquipmentFromResultSet(rs));
        
        return equipment;
    }
    
    // Вспомогательные методы для маппинга связанных объектов
    private Customer mapCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getLong("customer_id"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setMiddleName(rs.getString("middle_name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        return customer;
    }
    
    private Car mapCarFromResultSet(ResultSet rs) throws SQLException {
        Car car = new Car();
        car.setId(rs.getLong("car_id"));
        car.setBrand(rs.getString("brand"));
        car.setModel(rs.getString("model"));
        car.setVin(rs.getString("vin"));
        car.setPrice(rs.getBigDecimal("price"));
        return car;
    }
    
    private User mapUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("manager_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        return user;
    }
    
    private AdditionalEquipment mapAdditionalEquipmentFromResultSet(ResultSet rs) throws SQLException {
        AdditionalEquipment equipment = new AdditionalEquipment();
        equipment.setId(rs.getLong("equipment_id"));
        equipment.setName(rs.getString("name"));
        equipment.setPrice(rs.getBigDecimal("price"));
        return equipment;
    }
} 