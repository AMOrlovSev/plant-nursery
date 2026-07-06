-- changeset amorlov:8

CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    customer_email VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_price NUMERIC(19, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    plant_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    price_at_purchase NUMERIC(19, 2) NOT NULL,
    CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_order_items_plant FOREIGN KEY (plant_id) REFERENCES plants (id)
);

CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_plant_id ON order_items(plant_id);