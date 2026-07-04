-- changeset amorlov:7
ALTER TABLE suppliers ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;