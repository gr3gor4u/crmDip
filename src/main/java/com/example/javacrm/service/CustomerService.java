package com.example.javacrm.service;

import java.sql.*;

public class CustomerService {
    private static final String DB_URL = "jdbc:h2:./crmdb";
    private static final String USER = "sa";
    private static final String PASS = "";

    public long getTotalCustomers() {
        String sql = "SELECT COUNT(*) FROM customers";
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

    public long getNewCustomersToday() {
        String sql = "SELECT COUNT(*) FROM customers WHERE DATE(created_at) = CURRENT_DATE";
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
} 