package com.example.javacrm.service;

import com.example.javacrm.model.AdditionalEquipment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentService {
    private final DatabaseService databaseService;
    
    public EquipmentService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }
    
    public ObservableList<AdditionalEquipment> getAllEquipment() {
        List<AdditionalEquipment> equipment = new ArrayList<>();
        String query = "SELECT * FROM additional_equipment";
        
        try (Connection conn = databaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                AdditionalEquipment item = new AdditionalEquipment(
                    rs.getLong("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getBoolean("available")
                );
                equipment.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(equipment);
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
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getBoolean("available")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void addEquipment(AdditionalEquipment equipment) {
        String query = "INSERT INTO additional_equipment (name, price, quantity, available) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, equipment.getName());
            pstmt.setDouble(2, equipment.getPrice());
            pstmt.setInt(3, equipment.getQuantity());
            pstmt.setBoolean(4, equipment.getQuantity() > 0);
            
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
        String query = "UPDATE additional_equipment SET name = ?, price = ?, quantity = ?, available = ? WHERE id = ?";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, equipment.getName());
            pstmt.setDouble(2, equipment.getPrice());
            pstmt.setInt(3, equipment.getQuantity());
            pstmt.setBoolean(4, equipment.getQuantity() > 0);
            pstmt.setLong(5, equipment.getId());
            
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
    
    public List<AdditionalEquipment> searchEquipment(String id, String name, String minPrice, String maxPrice) {
        List<AdditionalEquipment> equipment = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM additional_equipment WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (id != null && !id.isEmpty()) {
            sql.append(" AND id = ?");
            params.add(Long.parseLong(id));
        }
        if (name != null && !name.isEmpty()) {
            sql.append(" AND name ILIKE ?");
            params.add("%" + name + "%");
        }
        if (minPrice != null && !minPrice.isEmpty()) {
            sql.append(" AND price >= ?");
            params.add(Double.parseDouble(minPrice));
        }
        if (maxPrice != null && !maxPrice.isEmpty()) {
            sql.append(" AND price <= ?");
            params.add(Double.parseDouble(maxPrice));
        }

        sql.append(" ORDER BY name");

        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                equipment.add(mapEquipmentFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске оборудования", e);
        }
        return equipment;
    }

    private AdditionalEquipment mapEquipmentFromResultSet(ResultSet rs) throws SQLException {
        return new AdditionalEquipment(
            rs.getLong("id"),
            rs.getString("name"),
            rs.getDouble("price"),
            rs.getInt("quantity"),
            rs.getBoolean("available")
        );
    }
} 