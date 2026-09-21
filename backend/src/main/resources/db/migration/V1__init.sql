-- V1__init.sql
-- RepairMatch initial baseline migration

CREATE TABLE IF NOT EXISTS system_metadata (
    id VARCHAR(50) PRIMARY KEY,
    key_name VARCHAR(100) NOT NULL UNIQUE,
    key_value VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO system_metadata (id, key_name, key_value)
VALUES ('meta-1', 'system_status', 'INITIALIZED');
