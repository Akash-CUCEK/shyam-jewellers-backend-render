CREATE TABLE material_type (
                               material_type_id BIGSERIAL PRIMARY KEY,
                               name VARCHAR(255) NOT NULL UNIQUE,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               created_by VARCHAR(255),
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_by VARCHAR(255),
                               status BOOLEAN NOT NULL,

                               making_charge_type VARCHAR(50) NOT NULL DEFAULT 'PERCENTAGE',
                               making_charge_value NUMERIC(10,2)
);