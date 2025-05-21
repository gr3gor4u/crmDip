-- Добавление поля notes в таблицу deals
ALTER TABLE deals
    ADD COLUMN IF NOT EXISTS notes TEXT; 