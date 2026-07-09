-- changeset amorlov:9

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    version BIGINT NOT NULL DEFAULT 0
);

-- Добавляем дефолтного админа (пароль: admin123)
INSERT INTO users (email, password, role, enabled, created_at, updated_at, version)
VALUES ('admin@nursery.com', '$2a$10$vG/yS2k7N6VbF1WJb7AFe.B5P9G.0bIeI2xY/dGk9P9N6C3yV3mTu', 'ROLE_ADMIN', TRUE, NOW(), NOW(), 0)
ON CONFLICT (email) DO NOTHING;