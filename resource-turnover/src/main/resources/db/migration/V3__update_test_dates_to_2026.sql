-- Оновлюємо дати в існуючих тестових замовленнях з 2025 на 2026 рік, щоб дедлайни не підсвічувались червоним як "минулі"
UPDATE orders
SET 
    delivery_date = delivery_date + interval '1 year',
    creation_date = creation_date + interval '1 year',
    completion_date = CASE WHEN completion_date IS NOT NULL THEN completion_date + interval '1 year' ELSE NULL END
WHERE EXTRACT(YEAR FROM delivery_date) = 2025;
