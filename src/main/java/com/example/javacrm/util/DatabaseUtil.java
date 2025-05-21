package com.example.javacrm.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseUtil {
    private static final String URL = "jdbc:postgresql://localhost:5432/javacrm";
    private static final Properties props = new Properties();
    
    static {
        props.setProperty("user", "postgres");
        props.setProperty("password", "postgres");
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, props);
    }
} 