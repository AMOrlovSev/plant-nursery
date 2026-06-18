--liquibase formatted sql

-- changeset artemorlov:4

INSERT INTO suppliers (name, contact_email, phone_number, created_at) VALUES
('ЭкоСад Питомник', 'info@ecosad.ru', '+7 (812) 111-22-33', NOW()),
('Зеленый Горизонт', 'sales@greenhorizon.ru', '+7 (495) 444-55-66', NOW()),
('ИмпортФлора', 'import@importflora.com', '+7 (800) 555-35-35', NOW()),
('Северное Сияние Растения', 'nord@nordplant.ru', '+7 (812) 777-88-99', NOW()),
('Садовый Мир Ферма', 'ferma@sadmir.ru', '+7 (921) 999-00-11', NOW());