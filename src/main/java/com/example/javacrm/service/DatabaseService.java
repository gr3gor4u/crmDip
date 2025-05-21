package com.example.javacrm.service;

import com.example.javacrm.model.*;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

public class DatabaseService {
    private static DatabaseService instance;
    private final String dbUrl;
    private final String dbUser;
    private final String dbPassword;
    private final String dbDriver;

    private DatabaseService() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find database.properties");
            }
            props.load(input);
            
            dbUrl = props.getProperty("db.url");
            dbUser = props.getProperty("db.user");
            dbPassword = props.getProperty("db.password");
            dbDriver = props.getProperty("db.driver");
            
            // Загружаем драйвер PostgreSQL
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("PostgreSQL JDBC Driver not found", e);
            }
            
            // Создаем подключение к базе данных
            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
                // База данных будет создана автоматически при первом подключении
                initializeDatabase();
            }
        } catch (SQLException | IOException e) {
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
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    public void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Создаем таблицу customers, если она не существует
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS customers (
                    id BIGSERIAL PRIMARY KEY,
                    first_name VARCHAR(50) NOT NULL,
                    last_name VARCHAR(50) NOT NULL,
                    middle_name VARCHAR(50),
                    marital_status VARCHAR(20),
                    children_count INTEGER DEFAULT 0,
                    email VARCHAR(100),
                    phone VARCHAR(20),
                    passport_number VARCHAR(20),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
            
            // Создаем таблицу additional_equipment, если она не существует
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS additional_equipment (
                    id BIGSERIAL PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    price DECIMAL(10,2) NOT NULL,
                    quantity INTEGER NOT NULL DEFAULT 0,
                    available BOOLEAN NOT NULL DEFAULT TRUE
                )
            """);
            
            // Создаем таблицу cars, если она не существует
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS cars (
                    id BIGSERIAL PRIMARY KEY,
                    vin VARCHAR(17) NOT NULL UNIQUE,
                    brand VARCHAR(50) NOT NULL,
                    model VARCHAR(50) NOT NULL,
                    year INTEGER NOT NULL,
                    color VARCHAR(30),
                    price DECIMAL(10,2) NOT NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
            
            // Создаем таблицу insurance, если она не существует
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS insurance (
                    id BIGSERIAL PRIMARY KEY,
                    customer_id BIGINT NOT NULL,
                    car_vin VARCHAR(17) NOT NULL,
                    insurance_type VARCHAR(50) NOT NULL,
                    insurance_number VARCHAR(50) NOT NULL,
                    start_date DATE NOT NULL,
                    end_date DATE NOT NULL,
                    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (customer_id) REFERENCES customers(id),
                    FOREIGN KEY (car_vin) REFERENCES cars(vin)
                )
            """);
            
            // Создаем таблицу users, если она не существует
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id BIGSERIAL PRIMARY KEY,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    password VARCHAR(100) NOT NULL,
                    email VARCHAR(100),
                    role VARCHAR(20) NOT NULL,
                    first_name VARCHAR(50),
                    last_name VARCHAR(50)
                )
            """);
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

    // Методы для работы с автомобилями
    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Car car = new Car();
                car.setId(rs.getLong("id"));
                car.setVin(rs.getString("vin"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setYear(rs.getInt("year"));
                car.setPrice(rs.getBigDecimal("price"));
                car.setColor(rs.getString("color"));
                car.setKuzov(rs.getString("kuzov"));
                car.setEngineVolume(rs.getDouble("obem_dvig"));
                car.setHorsePower(rs.getInt("horse_power"));
                car.setStatus(rs.getString("status"));
                car.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                cars.add(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get cars", e);
        }
        
        return cars;
    }

    public Car getCarById(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM cars WHERE id = ?")) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Car car = new Car();
                car.setId(rs.getLong("id"));
                car.setVin(rs.getString("vin"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setYear(rs.getInt("year"));
                car.setPrice(rs.getBigDecimal("price"));
                car.setColor(rs.getString("color"));
                car.setKuzov(rs.getString("kuzov"));
                car.setEngineVolume(rs.getDouble("obem_dvig"));
                car.setHorsePower(rs.getInt("horse_power"));
                car.setStatus(rs.getString("status"));
                return car;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get car by id", e);
        }
        return null;
    }

    public void addCar(Car car) {
        String sql = "INSERT INTO cars (vin, brand, model, year, price, color, kuzov, obem_dvig, horse_power, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, car.getVin());
            pstmt.setString(2, car.getBrand());
            pstmt.setString(3, car.getModel());
            pstmt.setInt(4, car.getYear());
            pstmt.setDouble(5, car.getPrice().doubleValue());
            pstmt.setString(6, car.getColor());
            pstmt.setString(7, car.getKuzov());
            pstmt.setDouble(8, car.getEngineVolume().doubleValue());
            pstmt.setInt(9, car.getHorsePower());
            pstmt.setString(10, car.getStatus());
            pstmt.setTimestamp(11, Timestamp.valueOf(car.getCreatedAt()));
            
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                car.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCar(Car car) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE cars SET vin = ?, brand = ?, model = ?, year = ?, color = ?, " +
                 "price = ?, kuzov = ?, obem_dvig = ?, horse_power = ?, status = ? WHERE id = ?")) {
            
            stmt.setString(1, car.getVin());
            stmt.setString(2, car.getBrand());
            stmt.setString(3, car.getModel());
            stmt.setInt(4, car.getYear());
            stmt.setString(5, car.getColor());
            stmt.setBigDecimal(6, car.getPrice());
            stmt.setString(7, car.getKuzov());
            stmt.setDouble(8, car.getEngineVolume());
            stmt.setInt(9, car.getHorsePower());
            stmt.setString(10, car.getStatus());
            stmt.setLong(11, car.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update car", e);
        }
    }

    public void deleteCar(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM cars WHERE id = ?")) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete car", e);
        }
    }

    public List<Car> searchCars(String vin, String brand, String model, String color, String status) {
        List<Car> cars = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM cars WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (vin != null && !vin.isEmpty()) {
            sql.append(" AND vin ILIKE ?");
            params.add("%" + vin + "%");
        }
        if (brand != null && !brand.isEmpty()) {
            sql.append(" AND brand ILIKE ?");
            params.add("%" + brand + "%");
        }
        if (model != null && !model.isEmpty()) {
            sql.append(" AND model ILIKE ?");
            params.add("%" + model + "%");
        }
        if (color != null && !color.isEmpty()) {
            sql.append(" AND color ILIKE ?");
            params.add("%" + color + "%");
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Car car = new Car();
                car.setId(rs.getLong("id"));
                car.setVin(rs.getString("vin"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setYear(rs.getInt("year"));
                car.setPrice(rs.getBigDecimal("price"));
                car.setColor(rs.getString("color"));
                car.setKuzov(rs.getString("kuzov"));
                car.setEngineVolume(rs.getDouble("obem_dvig"));
                car.setHorsePower(rs.getInt("horse_power"));
                car.setStatus(rs.getString("status"));
                cars.add(car);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search cars", e);
        }
        return cars;
    }

    // Методы для работы со сделками
    public Long saveDeal(Deal deal) {
        String sql = "INSERT INTO deals (customer_id, car_id, manager_id, total_price, status, deal_date, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, deal.getCustomerId());
            stmt.setLong(2, deal.getCarId());
            stmt.setLong(3, deal.getManagerId());
            stmt.setBigDecimal(4, deal.getTotalPrice());
            stmt.setString(5, deal.getStatus());
            stmt.setDate(6, java.sql.Date.valueOf(deal.getDealDate()));
            stmt.setString(7, deal.getNotes());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            throw new RuntimeException("Failed to get generated deal ID");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save deal", e);
        }
    }

    public void saveDealEquipment(DealEquipment equipment) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO deal_equipment (deal_id, equipment_id, quantity, price) VALUES (?, ?, ?, ?)")) {
            
            stmt.setLong(1, equipment.getDealId());
            stmt.setLong(2, equipment.getEquipmentId());
            stmt.setInt(3, equipment.getQuantity());
            stmt.setBigDecimal(4, equipment.getPrice());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save deal equipment", e);
        }
    }

    public List<Deal> getAllDeals() {
        List<Deal> deals = new ArrayList<>();
        String sql = "SELECT d.*, c.first_name, c.last_name, c.middle_name, " +
                    "car.brand, car.model, car.vin " +
                    "FROM deals d " +
                    "JOIN customers c ON d.customer_id = c.id " +
                    "JOIN cars car ON d.car_id = car.id";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Deal deal = new Deal();
                deal.setId(rs.getLong("id"));
                deal.setCustomerId(rs.getLong("customer_id"));
                deal.setCarId(rs.getLong("car_id"));
                deal.setManagerId(rs.getLong("manager_id"));
                deal.setTotalPrice(rs.getBigDecimal("total_price"));
                deal.setStatus(rs.getString("status"));
                deal.setDealDate(rs.getDate("deal_date").toLocalDate());
                deal.setNotes(rs.getString("notes"));
                
                // Set customer
                Customer customer = new Customer();
                customer.setId(rs.getLong("customer_id"));
                customer.setFirstName(rs.getString("first_name"));
                customer.setLastName(rs.getString("last_name"));
                customer.setMiddleName(rs.getString("middle_name"));
                deal.setCustomer(customer);
                
                // Set car
                Car car = new Car();
                car.setId(rs.getLong("car_id"));
                car.setBrand(rs.getString("brand"));
                car.setModel(rs.getString("model"));
                car.setVin(rs.getString("vin"));
                deal.setCar(car);
                
                deals.add(deal);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get deals", e);
        }
        return deals;
    }

    public Optional<Deal> getDealById(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM deals WHERE id = ?")) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
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
                    return Optional.of(deal);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void updateDeal(Deal deal) {
        String sql = "UPDATE deals SET customer_id = ?, car_id = ?, manager_id = ?, " +
                    "total_price = ?, status = ?, deal_date = ?, notes = ? WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, deal.getCustomerId());
            stmt.setLong(2, deal.getCarId());
            stmt.setLong(3, deal.getManagerId());
            stmt.setBigDecimal(4, deal.getTotalPrice());
            stmt.setString(5, deal.getStatus());
            stmt.setDate(6, java.sql.Date.valueOf(deal.getDealDate()));
            stmt.setString(7, deal.getNotes());
            stmt.setLong(8, deal.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update deal", e);
        }
    }

    public void deleteDeal(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM deals WHERE id = ?")) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete deal", e);
        }
    }

    public List<Deal> searchDeals(String customerName, String carModel, String status) {
        List<Deal> deals = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT d.* FROM deals d " +
            "JOIN customers c ON d.customer_id = c.id " +
            "JOIN cars car ON d.car_id = car.id " +
            "WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (customerName != null && !customerName.isEmpty()) {
            sql.append(" AND (c.first_name ILIKE ? OR c.last_name ILIKE ?)");
            params.add("%" + customerName + "%");
            params.add("%" + customerName + "%");
        }
        if (carModel != null && !carModel.isEmpty()) {
            sql.append(" AND car.model ILIKE ?");
            params.add("%" + carModel + "%");
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND d.status = ?");
            params.add(status);
        }
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
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
                deals.add(deal);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search deals", e);
        }
        return deals;
    }

    // Методы для работы с пользователями
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users ORDER BY username")) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get users", e);
        }
        return users;
    }

    public Optional<User> getUserById(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM users WHERE id = ?")) {
            stmt.setLong(1, id);
            
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
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public User saveUser(User user) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO users (username, password, email, role, first_name, last_name) " +
                 "VALUES (?, ?, ?, ?, ?, ?) RETURNING id")) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getFirstName());
            stmt.setString(6, user.getLastName());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user.setId(rs.getLong(1));
            }
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    public void updateUser(User user) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE users SET username = ?, password = ?, email = ?, role = ?, " +
                 "first_name = ?, last_name = ? WHERE id = ?")) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRole());
            stmt.setString(5, user.getFirstName());
            stmt.setString(6, user.getLastName());
            stmt.setLong(7, user.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }

    public void deleteUser(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    // Методы для работы со страховками
    public List<Insurance> getAllInsurance() {
        List<Insurance> insurances = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM insurance ORDER BY start_date DESC")) {
            
            while (rs.next()) {
                Insurance insurance = new Insurance();
                insurance.setId(rs.getLong("id"));
                insurance.setCustomerId(rs.getLong("customer_id"));
                insurance.setCarVin(rs.getString("car_vin"));
                insurance.setInsuranceType(Insurance.InsuranceType.valueOf(rs.getString("insurance_type")));
                insurance.setInsuranceNumber(rs.getString("insurance_number"));
                insurance.setStartDate(rs.getDate("start_date").toLocalDate());
                insurance.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
                insurance.setStatus(rs.getString("status"));
                insurances.add(insurance);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get insurances", e);
        }
        return insurances;
    }

    public Insurance getInsuranceById(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM insurance WHERE id = ?")) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Insurance insurance = new Insurance();
                insurance.setId(rs.getLong("id"));
                insurance.setCustomerId(rs.getLong("customer_id"));
                insurance.setCarVin(rs.getString("car_vin"));
                insurance.setInsuranceType(Insurance.InsuranceType.valueOf(rs.getString("insurance_type")));
                insurance.setInsuranceNumber(rs.getString("insurance_number"));
                insurance.setStartDate(rs.getDate("start_date").toLocalDate());
                insurance.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
                insurance.setStatus(rs.getString("status"));
                return insurance;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get insurance by id", e);
        }
        return null;
    }

    public Insurance saveInsurance(Insurance insurance) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO insurance (customer_id, car_vin, insurance_type, insurance_number, " +
                 "start_date, expiry_date, status) VALUES (?, ?, ?::insurance_type, ?, ?, ?, ?) RETURNING id")) {
            
            stmt.setLong(1, insurance.getCustomerId());
            stmt.setString(2, insurance.getCarVin());
            stmt.setString(3, insurance.getInsuranceType().name());
            stmt.setString(4, insurance.getInsuranceNumber());
            stmt.setDate(5, java.sql.Date.valueOf(insurance.getStartDate()));
            stmt.setDate(6, java.sql.Date.valueOf(insurance.getExpiryDate()));
            stmt.setString(7, insurance.getStatus());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                insurance.setId(rs.getLong(1));
            }
            return insurance;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save insurance", e);
        }
    }

    public void updateInsurance(Insurance insurance) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE insurance SET customer_id = ?, car_vin = ?, insurance_type = ?::insurance_type, " +
                 "insurance_number = ?, start_date = ?, expiry_date = ?, status = ? WHERE id = ?")) {
            
            stmt.setLong(1, insurance.getCustomerId());
            stmt.setString(2, insurance.getCarVin());
            stmt.setString(3, insurance.getInsuranceType().name());
            stmt.setString(4, insurance.getInsuranceNumber());
            stmt.setDate(5, java.sql.Date.valueOf(insurance.getStartDate()));
            stmt.setDate(6, java.sql.Date.valueOf(insurance.getExpiryDate()));
            stmt.setString(7, insurance.getStatus());
            stmt.setLong(8, insurance.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update insurance", e);
        }
    }

    public void deleteInsurance(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM insurance WHERE id = ?")) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete insurance", e);
        }
    }

    public List<Insurance> searchInsurance(String customerName, String carVin, String status) {
        List<Insurance> insurances = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT i.* FROM insurance i " +
            "JOIN customers c ON i.customer_id = c.id " +
            "WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (customerName != null && !customerName.isEmpty()) {
            sql.append(" AND (c.first_name ILIKE ? OR c.last_name ILIKE ?)");
            params.add("%" + customerName + "%");
            params.add("%" + customerName + "%");
        }
        if (carVin != null && !carVin.isEmpty()) {
            sql.append(" AND i.car_vin ILIKE ?");
            params.add("%" + carVin + "%");
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND i.status = ?");
            params.add(status);
        }
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Insurance insurance = new Insurance();
                insurance.setId(rs.getLong("id"));
                insurance.setCustomerId(rs.getLong("customer_id"));
                insurance.setCarVin(rs.getString("car_vin"));
                insurance.setInsuranceType(Insurance.InsuranceType.valueOf(rs.getString("insurance_type")));
                insurance.setInsuranceNumber(rs.getString("insurance_number"));
                insurance.setStartDate(rs.getDate("start_date").toLocalDate());
                insurance.setExpiryDate(rs.getDate("expiry_date").toLocalDate());
                insurance.setStatus(rs.getString("status"));
                insurances.add(insurance);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search insurances", e);
        }
        return insurances;
    }

    // Методы для работы с оборудованием
    public List<AdditionalEquipment> getAllEquipment() {
        List<AdditionalEquipment> equipment = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM additional_equipment ORDER BY name")) {
            
            while (rs.next()) {
                AdditionalEquipment item = new AdditionalEquipment();
                item.setId(rs.getLong("id"));
                item.setName(rs.getString("name"));
                item.setDescription(rs.getString("description"));
                item.setPrice(rs.getBigDecimal("price"));
                item.setQuantity(rs.getInt("quantity"));
                equipment.add(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get equipment", e);
        }
        return equipment;
    }

    public Optional<AdditionalEquipment> getEquipmentById(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT * FROM additional_equipment WHERE id = ?")) {
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    AdditionalEquipment equipment = new AdditionalEquipment();
                    equipment.setId(rs.getLong("id"));
                    equipment.setName(rs.getString("name"));
                    equipment.setPrice(rs.getBigDecimal("price"));
                    equipment.setQuantity(rs.getInt("quantity"));
                    equipment.setAvailable(rs.getBoolean("available"));
                    return Optional.of(equipment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public AdditionalEquipment saveEquipment(AdditionalEquipment equipment) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO additional_equipment (name, description, price, quantity) " +
                 "VALUES (?, ?, ?, ?) RETURNING id")) {
            
            stmt.setString(1, equipment.getName());
            stmt.setString(2, equipment.getDescription());
            stmt.setBigDecimal(3, equipment.getPrice());
            stmt.setInt(4, equipment.getQuantity());
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                equipment.setId(rs.getLong(1));
            }
            return equipment;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save equipment", e);
        }
    }

    public void updateEquipment(AdditionalEquipment equipment) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "UPDATE additional_equipment SET name = ?, description = ?, price = ?, quantity = ? " +
                 "WHERE id = ?")) {
            
            stmt.setString(1, equipment.getName());
            stmt.setString(2, equipment.getDescription());
            stmt.setBigDecimal(3, equipment.getPrice());
            stmt.setInt(4, equipment.getQuantity());
            stmt.setLong(5, equipment.getId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update equipment", e);
        }
    }

    public void deleteEquipment(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM additional_equipment WHERE id = ?")) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete equipment", e);
        }
    }

    public List<AdditionalEquipment> searchEquipment(String name, BigDecimal minPrice, BigDecimal maxPrice) {
        List<AdditionalEquipment> equipment = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM additional_equipment WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (name != null && !name.isEmpty()) {
            sql.append(" AND name ILIKE ?");
            params.add("%" + name + "%");
        }
        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                AdditionalEquipment item = new AdditionalEquipment();
                item.setId(rs.getLong("id"));
                item.setName(rs.getString("name"));
                item.setDescription(rs.getString("description"));
                item.setPrice(rs.getBigDecimal("price"));
                item.setQuantity(rs.getInt("quantity"));
                equipment.add(item);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search equipment", e);
        }
        return equipment;
    }

    public void updateDealEquipment(DealEquipment equipment) {
        String sql = "UPDATE deal_equipment SET quantity = ?, price = ? WHERE deal_id = ? AND equipment_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, equipment.getQuantity());
            pstmt.setBigDecimal(2, equipment.getPrice());
            pstmt.setLong(3, equipment.getDealId());
            pstmt.setLong(4, equipment.getEquipmentId());
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении оборудования в сделке", e);
        }
    }
} 