-- Добавляем новые поля в таблицу deals
ALTER TABLE deals
    ADD COLUMN manager_id BIGINT REFERENCES users(id),
    ADD COLUMN deal_date DATE NOT NULL DEFAULT CURRENT_DATE,
    ADD COLUMN no_insurance BOOLEAN DEFAULT FALSE,
    ADD COLUMN insurance_number VARCHAR(10),
    DROP CONSTRAINT IF EXISTS deals_insurance_id_fkey,
    DROP COLUMN IF EXISTS insurance_id;

-- Добавляем поле в таблицу deal_equipment
ALTER TABLE deal_equipment
    ADD COLUMN no_equipment BOOLEAN DEFAULT FALSE;

-- Создаем индексы для новых полей
CREATE INDEX idx_deals_manager ON deals(manager_id);
CREATE INDEX idx_deals_date ON deals(deal_date);
CREATE INDEX idx_deals_insurance_number ON deals(insurance_number); 