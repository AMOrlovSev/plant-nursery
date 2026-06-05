--liquibase formatted sql

--changeset amorlov_sev:1
CREATE TABLE plants (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(100) NOT NULL,
    price NUMERIC(19, 2) NOT NULL,
    quantity INT NOT NULL
);