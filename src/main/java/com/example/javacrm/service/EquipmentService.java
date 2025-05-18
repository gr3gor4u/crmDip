package com.example.javacrm.service;

import com.example.javacrm.model.AdditionalEquipment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentService {
    private final DatabaseService databaseService;
    
    public EquipmentService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }
    
    public List<AdditionalEquipment> getAllEquipment() {
        List<AdditionalEquipment> equipment = new ArrayList<>();
        String query = "SELECT * FROM additional_equipment";
        
        try (Connection conn = databaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                AdditionalEquipment item = new AdditionalEquipment(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getString("type"),
                    rs.getString("status")
                );
                equipment.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipment;
    }
    
    public AdditionalEquipment getEquipmentById(Long id) {
        String query = "SELECT * FROM additional_equipment WHERE id = ?";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new AdditionalEquipment(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getString("type"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void addEquipment(AdditionalEquipment equipment) {
        String query = "INSERT INTO additional_equipment (name, description, price, type, status) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, equipment.getName());
            pstmt.setString(2, equipment.getDescription());
            pstmt.setDouble(3, equipment.getPrice());
            pstmt.setString(4, equipment.getType());
            pstmt.setString(5, equipment.getStatus());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    equipment.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateEquipment(AdditionalEquipment equipment) {
        String query = "UPDATE additional_equipment SET name = ?, description = ?, price = ?, type = ?, status = ? WHERE id = ?";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, equipment.getName());
            pstmt.setString(2, equipment.getDescription());
            pstmt.setDouble(3, equipment.getPrice());
            pstmt.setString(4, equipment.getType());
            pstmt.setString(5, equipment.getStatus());
            pstmt.setLong(6, equipment.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void deleteEquipment(Long id) {
        String query = "DELETE FROM additional_equipment WHERE id = ?";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public List<AdditionalEquipment> searchEquipment(String name, String type, String status) {
        List<AdditionalEquipment> equipment = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM additional_equipment WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (name != null && !name.isEmpty()) {
            query.append(" AND name LIKE ?");
            params.add("%" + name + "%");
        }
        
        if (type != null && !type.isEmpty()) {
            query.append(" AND type LIKE ?");
            params.add("%" + type + "%");
        }
        
        if (status != null && !status.isEmpty()) {
            query.append(" AND status = ?");
            params.add(status);
        }
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                AdditionalEquipment item = new AdditionalEquipment(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getString("type"),
                    rs.getString("status")
                );
                equipment.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return equipment;
    }
} 