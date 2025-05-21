package com.example.javacrm.service;

import com.example.javacrm.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerService {
    private final DatabaseService databaseService;

    public CustomerService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY last_name, first_name";
        
        try (Connection conn = databaseService.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                customers.add(mapCustomerFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all customers", e);
        }
        return customers;
    }

    public Customer getCustomerById(Long id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapCustomerFromResultSet(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting customer by id", e);
        }
        return null;
    }

    public List<Customer> searchCustomers(String id, String firstName, String lastName, String middleName, 
                                        String email, String passportNumber, String phone) {
        List<Customer> customers = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM customers WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (id != null && !id.isEmpty()) {
            sql.append(" AND id = ?");
            params.add(Long.parseLong(id));
        }
        if (firstName != null && !firstName.isEmpty()) {
            sql.append(" AND first_name ILIKE ?");
            params.add("%" + firstName + "%");
        }
        if (lastName != null && !lastName.isEmpty()) {
            sql.append(" AND last_name ILIKE ?");
            params.add("%" + lastName + "%");
        }
        if (middleName != null && !middleName.isEmpty()) {
            sql.append(" AND middle_name ILIKE ?");
            params.add("%" + middleName + "%");
        }
        if (email != null && !email.isEmpty()) {
            sql.append(" AND email ILIKE ?");
            params.add("%" + email + "%");
        }
        if (passportNumber != null && !passportNumber.isEmpty()) {
            sql.append(" AND passport_number ILIKE ?");
            params.add("%" + passportNumber + "%");
        }
        if (phone != null && !phone.isEmpty()) {
            sql.append(" AND phone ILIKE ?");
            params.add("%" + phone + "%");
        }

        sql.append(" ORDER BY last_name, first_name");

        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                customers.add(mapCustomerFromResultSet(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске клиентов", e);
        }
        return customers;
    }

    public void addCustomer(Customer customer) {
        String sql = "INSERT INTO customers (first_name, last_name, middle_name, marital_status, " +
                    "children_count, email, phone, passport_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getMiddleName());
            pstmt.setString(4, customer.getMaritalStatus());
            pstmt.setInt(5, customer.getChildrenCount());
            pstmt.setString(6, customer.getEmail());
            pstmt.setString(7, customer.getPhone());
            pstmt.setString(8, customer.getPassportNumber());
            
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    customer.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding customer", e);
        }
    }

    public void updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, middle_name = ?, " +
                    "marital_status = ?, children_count = ?, email = ?, phone = ?, passport_number = ? WHERE id = ?";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getMiddleName());
            pstmt.setString(4, customer.getMaritalStatus());
            pstmt.setInt(5, customer.getChildrenCount());
            pstmt.setString(6, customer.getEmail());
            pstmt.setString(7, customer.getPhone());
            pstmt.setString(8, customer.getPassportNumber());
            pstmt.setLong(9, customer.getId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating customer", e);
        }
    }

    public void deleteCustomer(Long id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        
        try (Connection conn = databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting customer", e);
        }
    }

    private Customer mapCustomerFromResultSet(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setMiddleName(rs.getString("middle_name"));
        customer.setMaritalStatus(rs.getString("marital_status"));
        customer.setChildrenCount(rs.getInt("children_count"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setPassportNumber(rs.getString("passport_number"));
        customer.setCreatedAt(rs.getString("created_at"));
        return customer;
    }
} 