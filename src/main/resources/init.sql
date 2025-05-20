-- Удаляем существующие таблицы
DROP TABLE IF EXISTS deal_equipment;
DROP TABLE IF EXISTS deals;
DROP TABLE IF EXISTS insurance;
DROP TABLE IF EXISTS additional_equipment;
DROP TABLE IF EXISTS cars;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS users;
DROP TYPE IF EXISTS insurance_type;

-- Создаем таблицы заново
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
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

CREATE TABLE cars (
    id BIGSERIAL PRIMARY KEY,
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

CREATE TABLE additional_equipment (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    type VARCHAR(50),
    quantity INTEGER NOT NULL DEFAULT 0,
    available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Создание enum для типа страховки
CREATE TYPE insurance_type AS ENUM ('КАСКО', 'ОСАГО');

-- Функция для генерации случайного номера страховки
CREATE OR REPLACE FUNCTION generate_insurance_number()
RETURNS TRIGGER AS $$
DECLARE
    chars TEXT := 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    result TEXT := '';
    i INTEGER;
BEGIN
    FOR i IN 1..10 LOOP
        result := result || substr(chars, floor(random() * length(chars) + 1)::integer, 1);
    END LOOP;
    NEW.insurance_number := result;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Создание таблицы страховок
CREATE TABLE insurance (
    id SERIAL PRIMARY KEY,
    customer_id BIGINT REFERENCES customers(id),
    car_vin VARCHAR(17) REFERENCES cars(vin),
    insurance_type insurance_type NOT NULL,
    insurance_number VARCHAR(10) NOT NULL UNIQUE,
    start_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL CHECK (status IN ('Активна', 'Истекла')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Триггер для автоматической генерации номера страховки
CREATE TRIGGER set_insurance_number
    BEFORE INSERT ON insurance
    FOR EACH ROW
    WHEN (NEW.insurance_number IS NULL)
    EXECUTE FUNCTION generate_insurance_number();

CREATE TABLE deals (
    id BIGSERIAL PRIMARY KEY,
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

CREATE TABLE deal_equipment (
    deal_id BIGINT NOT NULL,
    equipment_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (deal_id, equipment_id),
    FOREIGN KEY (deal_id) REFERENCES deals(id),
    FOREIGN KEY (equipment_id) REFERENCES additional_equipment(id)
);

-- Добавляем индексы
CREATE INDEX idx_customers_name ON customers(first_name, last_name, middle_name);
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_passport ON customers(passport_number);
CREATE INDEX idx_cars_vin ON cars(vin);
CREATE INDEX idx_insurance_number ON insurance(insurance_number);
CREATE INDEX idx_deals_status ON deals(status);

-- Создание индексов
CREATE INDEX idx_insurance_customer_id ON insurance(customer_id);
CREATE INDEX idx_insurance_car_vin ON insurance(car_vin);
CREATE INDEX idx_insurance_status ON insurance(status);
CREATE INDEX idx_insurance_dates ON insurance(start_date, expiry_date);

-- Добавляем тестового пользователя
INSERT INTO users (username, password, email, role, first_name, last_name)
VALUES ('admin', 'admin', 'admin@example.com', 'ADMIN', 'Admin', 'User')
ON CONFLICT (username) DO NOTHING;

DO $$ BEGIN
    CREATE TYPE insurance_status AS ENUM ('Активна', 'Истекла');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$; 