package com.example.javacrm.service;

import com.example.javacrm.model.Deal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DealService {
    private static final String DB_URL = "jdbc:h2:./crmdb";
    private static final String USER = "sa";
    private static final String PASS = "";

    public long getTotalDeals() {
        String sql = "SELECT COUNT(*) FROM deals";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public long getActiveDeals() {
        String sql = "SELECT COUNT(*) FROM deals WHERE status = 'ACTIVE'";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Deal> getRecentDeals(int limit) {
        List<Deal> deals = new ArrayList<>();
        String sql = "SELECT d.*, c.name as customer_name, car.model as car_model " +
                    "FROM deals d " +
                    "JOIN customers c ON d.customer_id = c.id " +
                    "JOIN cars car ON d.car_id = car.id " +
                    "ORDER BY d.date DESC LIMIT ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Deal deal = new Deal();
                deal.setId(rs.getLong("id"));
                deal.setCustomerName(rs.getString("customer_name"));
                deal.setCarModel(rs.getString("car_model"));
                deal.setAmount(rs.getDouble("amount"));
                deal.setDate(rs.getDate("date").toLocalDate());
                deals.add(deal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deals;
    }

    public List<Deal> getMonthlyDeals(int months) {
        List<Deal> deals = new ArrayList<>();
        String sql = "SELECT d.*, c.name as customer_name, car.model as car_model " +
                    "FROM deals d " +
                    "JOIN customers c ON d.customer_id = c.id " +
                    "JOIN cars car ON d.car_id = car.id " +
                    "WHERE d.date >= DATEADD('MONTH', ?, CURRENT_DATE) " +
                    "ORDER BY d.date";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, -months);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Deal deal = new Deal();
                deal.setId(rs.getLong("id"));
                deal.setCustomerName(rs.getString("customer_name"));
                deal.setCarModel(rs.getString("car_model"));
                deal.setAmount(rs.getDouble("amount"));
                deal.setDate(rs.getDate("date").toLocalDate());
                deals.add(deal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return deals;
    }
} 