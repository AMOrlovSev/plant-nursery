--liquibase formatted sql

-- changeset artemorlov:3

CREATE TABLE suppliers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    contact_email VARCHAR(255),
    phone_number VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

ALTER TABLE plants ADD COLUMN supplier_id BIGINT;

ALTER TABLE plants
    ADD CONSTRAINT fk_plants_supplier
    FOREIGN KEY (supplier_id)
    REFERENCES suppliers(id)
    ON DELETE SET NULL;