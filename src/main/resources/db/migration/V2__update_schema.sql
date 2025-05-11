-- Обновление таблицы cars
ALTER TABLE cars
    ADD COLUMN IF NOT EXISTS kuzov VARCHAR(50),
    ADD COLUMN IF NOT EXISTS obem_dvig DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS horse_power INTEGER;

-- Создание таблицы additional_equipment
CREATE TABLE IF NOT EXISTS additional_equipment (
    price_dop DECIMAL(10,2) PRIMARY KEY,
    tonirovka VARCHAR(100),
    diski VARCHAR(100),
    second_komplekt VARCHAR(100),
    kovriki VARCHAR(100),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Создание таблицы trade_in_car
CREATE TABLE IF NOT EXISTS trade_in_car (
    vin_number_trade VARCHAR(17) PRIMARY KEY,
    model_car VARCHAR(100),
    price_car_trade DECIMAL(10,2),
    marka VARCHAR(100),
    type_kuzov VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Создание таблицы insurance
CREATE TABLE IF NOT EXISTS insurance (
    id_osago BIGSERIAL PRIMARY KEY,
    fio VARCHAR(200),
    data TIMESTAMP,
    seria_num_vod VARCHAR(20),
    pipl_dop_k_avto VARCHAR(200),
    strah_premia DECIMAL(10,2),
    price_osago DECIMAL(10,2),
    vin_number VARCHAR(17) REFERENCES cars(vin_number),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Создание таблицы deal_additional_equipment
CREATE TABLE IF NOT EXISTS deal_additional_equipment (
    deal_id BIGINT REFERENCES deals(id_dogovor),
    price_dop DECIMAL(10,2) REFERENCES additional_equipment(price_dop),
    count INTEGER,
    type_dop VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (deal_id, price_dop)
);

-- Обновление таблицы customers
ALTER TABLE customers
    ADD COLUMN IF NOT EXISTS mesto_rozhd VARCHAR(200),
    ADD COLUMN IF NOT EXISTS pasport_data VARCHAR(200);

-- Обновление таблицы deals
ALTER TABLE deals
    ADD COLUMN IF NOT EXISTS vin_number_trade VARCHAR(17) REFERENCES trade_in_car(vin_number_trade);

-- Создание индексов
CREATE INDEX IF NOT EXISTS idx_cars_vin ON cars(vin_number);
CREATE INDEX IF NOT EXISTS idx_customers_id ON customers(id_chelovek);
CREATE INDEX IF NOT EXISTS idx_deals_id ON deals(id_dogovor);
CREATE INDEX IF NOT EXISTS idx_insurance_vin ON insurance(vin_number);
CREATE INDEX IF NOT EXISTS idx_trade_in_vin ON trade_in_car(vin_number_trade);
CREATE INDEX IF NOT EXISTS idx_deal_equipment_deal ON deal_additional_equipment(deal_id);
CREATE INDEX IF NOT EXISTS idx_deal_equipment_price ON deal_additional_equipment(price_dop); 
ALTER TABLE cars
    ADD COLUMN IF NOT EXISTS kuzov VARCHAR(50),
    ADD COLUMN IF NOT EXISTS obem_dvig DOUBLE PRECISION,
    ADD COLUMN IF NOT EXISTS horse_power INTEGER;

-- Создание таблицы additional_equipment
CREATE TABLE IF NOT EXISTS additional_equipment (
    price_dop DECIMAL(10,2) PRIMARY KEY,
    tonirovka VARCHAR(100),
    diski VARCHAR(100),
    second_komplekt VARCHAR(100),
    kovriki VARCHAR(100),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Создание таблицы trade_in_car
CREATE TABLE IF NOT EXISTS trade_in_car (
    vin_number_trade VARCHAR(17) PRIMARY KEY,
    model_car VARCHAR(100),
    price_car_trade DECIMAL(10,2),
    marka VARCHAR(100),
    type_kuzov VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Создание таблицы insurance
CREATE TABLE IF NOT EXISTS insurance (
    id_osago BIGSERIAL PRIMARY KEY,
    fio VARCHAR(200),
    data TIMESTAMP,
    seria_num_vod VARCHAR(20),
    pipl_dop_k_avto VARCHAR(200),
    strah_premia DECIMAL(10,2),
    price_osago DECIMAL(10,2),
    vin_number VARCHAR(17) REFERENCES cars(vin_number),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Создание таблицы deal_additional_equipment
CREATE TABLE IF NOT EXISTS deal_additional_equipment (
    deal_id BIGINT REFERENCES deals(id_dogovor),
    price_dop DECIMAL(10,2) REFERENCES additional_equipment(price_dop),
    count INTEGER,
    type_dop VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    PRIMARY KEY (deal_id, price_dop)
);

-- Обновление таблицы customers
ALTER TABLE customers
    ADD COLUMN IF NOT EXISTS mesto_rozhd VARCHAR(200),
    ADD COLUMN IF NOT EXISTS pasport_data VARCHAR(200);

-- Обновление таблицы deals
ALTER TABLE deals
    ADD COLUMN IF NOT EXISTS vin_number_trade VARCHAR(17) REFERENCES trade_in_car(vin_number_trade);

-- Создание индексов
CREATE INDEX IF NOT EXISTS idx_cars_vin ON cars(vin_number);
CREATE INDEX IF NOT EXISTS idx_customers_id ON customers(id_chelovek);
CREATE INDEX IF NOT EXISTS idx_deals_id ON deals(id_dogovor);
CREATE INDEX IF NOT EXISTS idx_insurance_vin ON insurance(vin_number);
CREATE INDEX IF NOT EXISTS idx_trade_in_vin ON trade_in_car(vin_number_trade);
CREATE INDEX IF NOT EXISTS idx_deal_equipment_deal ON deal_additional_equipment(deal_id);
CREATE INDEX IF NOT EXISTS idx_deal_equipment_price ON deal_additional_equipment(price_dop); 