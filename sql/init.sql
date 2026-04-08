CREATE TABLE IF NOT EXISTS resources (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    type VARCHAR(100) NOT NULL,
    quantity NUMERIC(19,2) NOT NULL,
    price NUMERIC(19,2) NOT NULL,
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS suppliers (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact_info VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    resource_id BIGINT NOT NULL REFERENCES resources(id),
    quantity NUMERIC(19,2) NOT NULL,
    delivery_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    supplier_id BIGINT REFERENCES suppliers(id),
    creation_date DATE NOT NULL,
    completion_date DATE
);

CREATE TABLE IF NOT EXISTS order_comments (
    id SERIAL PRIMARY KEY,
    author VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    date DATE NOT NULL,
    order_id BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE
);

INSERT INTO resources (name, unit, type, quantity, price)
VALUES 
    ('Пшениця', 'кг', 'Зернові', 1000, 15.5),
    ('Кукурудза', 'кг', 'Зернові', 500, 12.3),
    ('Соняшникова олія', 'л', 'Масла', 300, 35.0),
    ('Цукор', 'кг', 'Продукти', 200, 20.0),
    ('Картопля', 'кг', 'Овочі', 800, 8.5)
ON CONFLICT DO NOTHING;

INSERT INTO suppliers (name, contact_info)
VALUES 
    ('ТОВ "АгроПостач"', 'agropostach@gmail.com'),
    ('ФГ "Зелений Гай"', 'zelhaifarm@ukr.net'),
    ('ТОВ "ЕкоПродукт"', 'eco-product@ukr.net')
ON CONFLICT DO NOTHING;

INSERT INTO orders (resource_id, quantity, delivery_date, status, supplier_id, creation_date, completion_date)
VALUES
    (1, 200, '2025-05-10', 'PENDING_EXECUTION', 1, '2025-05-01', NULL),
    (2, 100, '2025-05-15', 'COMPLETED', 2, '2025-05-02', '2025-05-04'),
    (3, 150, '2025-05-12', 'PENDING_SUPPLY', 3, '2025-05-03', NULL),
    (4, 80, '2025-05-13', 'IN_DELIVERY', 2, '2025-05-04', NULL),
    (5, 120, '2025-05-09', 'CANCELLED', 1, '2025-05-02', NULL)
ON CONFLICT DO NOTHING;

INSERT INTO order_comments (author, content, date, order_id)
VALUES 
    ('Оксана Коваль', 'Перевірити якість перед відвантаженням.', '2025-05-02', 1),
    ('Іван Петренко', 'Доставлено без пошкоджень.', '2025-05-04', 2),
    ('Наталія Шевчук', 'Уточнити кількість наступного разу.', '2025-05-05', 3)
ON CONFLICT DO NOTHING;
