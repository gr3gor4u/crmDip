package com.example.javacrm.service;

import com.example.javacrm.model.User;
import java.sql.*;
import java.util.Optional;

public class DatabaseService {
    private static final String DB_URL = "jdbc:h2:./crmdb";
    private static final String USER = "sa";
    private static final String PASS = "";
    private static DatabaseService instance;

    private DatabaseService() {
        initializeDatabase();
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
        try (Connection conn = getConnection()) {
            // Create users table
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(50) NOT NULL UNIQUE,
                        password VARCHAR(100) NOT NULL,
                        email VARCHAR(100) NOT NULL UNIQUE,
                        role VARCHAR(20) NOT NULL,
                        first_name VARCHAR(50) NOT NULL,
                        last_name VARCHAR(50) NOT NULL
                    )
                """);

                // Create customers table
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS customers (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(100) NOT NULL,
                        email VARCHAR(100),
                        phone VARCHAR(20),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """);

                // Create cars table
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS cars (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        model VARCHAR(100) NOT NULL,
                        brand VARCHAR(50) NOT NULL,
                        car_year INT NOT NULL,
                        price DECIMAL(10,2) NOT NULL,
                        status VARCHAR(20) DEFAULT 'AVAILABLE',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                    )
                """);

                // Create deals table
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS deals (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        customer_id BIGINT NOT NULL,
                        car_id BIGINT NOT NULL,
                        amount DECIMAL(10,2) NOT NULL,
                        date DATE NOT NULL,
                        status VARCHAR(20) DEFAULT 'ACTIVE',
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        FOREIGN KEY (customer_id) REFERENCES customers(id),
                        FOREIGN KEY (car_id) REFERENCES cars(id)
                    )
                """);

                // Insert test user if not exists
                stmt.execute("""
                    INSERT INTO users (username, password, email, role, first_name, last_name)
                    SELECT 'admin', 'admin', 'admin@example.com', 'ADMIN', 'Admin', 'User'
                    WHERE NOT EXISTS (
                        SELECT 1 FROM users WHERE username = 'admin'
                    )
                """);

                // Insert test customer
                stmt.execute("""
                    INSERT INTO customers (name, email, phone)
                    SELECT 'John Doe', 'john@example.com', '+1234567890'
                    WHERE NOT EXISTS (
                        SELECT 1 FROM customers WHERE email = 'john@example.com'
                    )
                """);

                // Insert test car
                stmt.execute("""
                    INSERT INTO cars (model, brand, car_year, price)
                    SELECT 'Model S', 'Tesla', 2023, 80000.00
                    WHERE NOT EXISTS (
                        SELECT 1 FROM cars WHERE model = 'Model S' AND brand = 'Tesla'
                    )
                """);

                // Insert test deal
                stmt.execute("""
                    INSERT INTO deals (customer_id, car_id, amount, date)
                    SELECT 
                        (SELECT id FROM customers WHERE email = 'john@example.com'),
                        (SELECT id FROM cars WHERE model = 'Model S'),
                        80000.00,
                        CURRENT_DATE
                    WHERE NOT EXISTS (
                        SELECT 1 FROM deals WHERE customer_id = (
                            SELECT id FROM customers WHERE email = 'john@example.com'
                        )
                    )
                """);
            }
        } catch (SQLException e) {
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