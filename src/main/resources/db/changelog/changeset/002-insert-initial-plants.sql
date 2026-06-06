--liquibase formatted sql

--changeset amorlov_sev:2
INSERT INTO plants (name, type, price, quantity) VALUES
('Голубая Ель', 'Хвойные', 5500.00, 10),
('Дуб Красный', 'Лиственные', 4200.00, 5),
('Туя Западная', 'Хвойные', 2100.00, 25);