package com.example.javacrm.service;

import com.example.javacrm.model.User;
import java.sql.*;
import java.util.Optional;

public class DatabaseService {
    private static final String DB_URL = "jdbc:h2:./crmdb;DB_CLOSE_ON_EXIT=FALSE";
    private static final String USER = "sa";
    private static final String PASS = "";
    private static DatabaseService instance;

    private DatabaseService() {
        try {
            Class.forName("org.h2.Driver");
            // Создаем подключение к базе данных
            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                // База данных будет создана автоматически при первом подключении
                initializeDatabase();
            }
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Читаем и выполняем schema.sql
            String schema = new String(getClass().getResourceAsStream("/schema.sql").readAllBytes());
            for (String sql : schema.split(";")) {
                if (!sql.trim().isEmpty()) {
                    try {
                        stmt.execute(sql);
                    } catch (SQLException e) {
                        // Log the error but continue with other statements
                        System.err.println("Error executing SQL: " + sql);
                        System.err.println("Error: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public Optional<User> authenticateUser(String username, String password) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT * FROM users WHERE username = ? AND password = ?")) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to authenticate user", e);
        }
        return Optional.empty();
    }

    public void registerUser(User user) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO users (username, password, email, role, first_name, last_name) VALUES (?, ?, ?, ?, ?, ?)")) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getFirstName());
            stmt.setString(6, user.getLastName());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to register user", e);
        }
    }
} 