-- Create insurance table
CREATE TABLE IF NOT EXISTS insurance (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    car_vin VARCHAR(17) NOT NULL,
    customer VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    number VARCHAR(50) NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (car_vin) REFERENCES cars(vin)
);

-- Create additional equipment table
CREATE TABLE IF NOT EXISTS additional_equipment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    type VARCHAR(50) NOT NULL
);

-- Create deal_equipment table for many-to-many relationship between deals and equipment
CREATE TABLE IF NOT EXISTS deal_equipment (
    deal_id BIGINT NOT NULL,
    equipment_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    PRIMARY KEY (deal_id, equipment_id),
    FOREIGN KEY (deal_id) REFERENCES deals(id),
    FOREIGN KEY (equipment_id) REFERENCES additional_equipment(id)
); 