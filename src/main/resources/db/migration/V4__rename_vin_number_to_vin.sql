-- Переименование столбца vin_number в vin в таблице cars
ALTER TABLE cars RENAME COLUMN vin_number TO vin;

-- Обновление внешних ключей в связанных таблицах (если есть)
-- Пример для insurance:
ALTER TABLE insurance DROP CONSTRAINT IF EXISTS insurance_car_vin_fkey;
ALTER TABLE insurance ADD CONSTRAINT insurance_car_vin_fkey FOREIGN KEY (car_vin) REFERENCES cars(vin); 