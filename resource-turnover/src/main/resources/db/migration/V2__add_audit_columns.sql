-- Додавання технічних колонок для оптимістичного блокування та аудиту в таблицю resources
ALTER TABLE resources 
ADD COLUMN version INTEGER DEFAULT 0 NOT NULL,
ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Для вже існуючого тестового наповнення можемо оновити поля
UPDATE resources SET version = 0, created_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP;
