-- Добавление колонки year в таблицу cars
ALTER TABLE cars ADD COLUMN IF NOT EXISTS year INTEGER; 