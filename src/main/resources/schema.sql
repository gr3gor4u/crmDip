-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица клиентов
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    middle_name VARCHAR(50),
    marital_status VARCHAR(20),
    children_count INT,
    email VARCHAR(100) UNIQUE,
    passport_number VARCHAR(20) UNIQUE,
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица автомобилей
CREATE TABLE IF NOT EXISTS cars (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    model VARCHAR(100) NOT NULL,
    brand VARCHAR(50) NOT NULL,
    car_year INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    vin VARCHAR(17) NOT NULL UNIQUE,
    color VARCHAR(50),
    kuzov VARCHAR(50),
    obem_dvig DOUBLE PRECISION,
    horse_power INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица дополнительного оборудования
CREATE TABLE IF NOT EXISTS additional_equipment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица страховок
CREATE TABLE IF NOT EXISTS insurance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    car_vin VARCHAR(17) NOT NULL,
    customer_id BIGINT NOT NULL,
    insurance_type VARCHAR(50) NOT NULL,
    insurance_number VARCHAR(50) NOT NULL UNIQUE,
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (car_vin) REFERENCES cars(vin),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Таблица сделок
CREATE TABLE IF NOT EXISTS deals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    car_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    insurance_id BIGINT,
    total_price DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (car_id) REFERENCES cars(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (insurance_id) REFERENCES insurance(id)
);

-- Таблица связи сделок с оборудованием
CREATE TABLE IF NOT EXISTS deal_equipment (
    deal_id BIGINT NOT NULL,
    equipment_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (deal_id, equipment_id),
    FOREIGN KEY (deal_id) REFERENCES deals(id),
    FOREIGN KEY (equipment_id) REFERENCES additional_equipment(id)
);

-- Добавляем VIN-номер в таблицу автомобилей
ALTER TABLE cars ADD COLUMN IF NOT EXISTS vin VARCHAR(17) UNIQUE;

-- Добавляем индексы для оптимизации поиска
CREATE INDEX IF NOT EXISTS idx_customers_name ON customers(first_name, last_name, middle_name);
CREATE INDEX IF NOT EXISTS idx_customers_email ON customers(email);
CREATE INDEX IF NOT EXISTS idx_customers_passport ON customers(passport_number);
CREATE INDEX IF NOT EXISTS idx_cars_vin ON cars(vin);
CREATE INDEX IF NOT EXISTS idx_insurance_number ON insurance(insurance_number);
CREATE INDEX IF NOT EXISTS idx_deals_status ON deals(status);

-- Тестовые данные
INSERT INTO users (username, password, email, role, first_name, last_name)
SELECT 'admin', 'admin', 'admin@example.com', 'ADMIN', 'Admin', 'User'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin'); 