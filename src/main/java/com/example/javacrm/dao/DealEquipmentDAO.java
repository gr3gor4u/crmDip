package com.example.javacrm.dao;

import com.example.javacrm.model.AdditionalEquipment;
import com.example.javacrm.model.DealEquipment;
import com.example.javacrm.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DealEquipmentDAO {
    
    public void create(DealEquipment equipment) throws SQLException {
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
    
    public List<DealEquipment> findByDealId(Long dealId) throws SQLException {
        String sql = "SELECT de.*, ae.* FROM deal_equipment de " +
                    "JOIN additional_equipment ae ON de.equipment_id = ae.id " +
                    "WHERE de.deal_id = ?";
                    
        List<DealEquipment> equipment = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, dealId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    equipment.add(mapFromResultSet(rs));
                }
            }
        }
        
        return equipment;
    }
    
    public void deleteByDealId(Long dealId) throws SQLException {
        String sql = "DELETE FROM deal_equipment WHERE deal_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, dealId);
            stmt.executeUpdate();
        }
    }
    
    private DealEquipment mapFromResultSet(ResultSet rs) throws SQLException {
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
    
    private AdditionalEquipment mapAdditionalEquipmentFromResultSet(ResultSet rs) throws SQLException {
        AdditionalEquipment equipment = new AdditionalEquipment();
        equipment.setId(rs.getLong("equipment_id"));
        equipment.setName(rs.getString("name"));
        equipment.setPrice(rs.getBigDecimal("price"));
        return equipment;
    }
} 