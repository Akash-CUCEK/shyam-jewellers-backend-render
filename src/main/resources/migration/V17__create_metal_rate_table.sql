CREATE TABLE metal_rate (
                            id BIGSERIAL PRIMARY KEY,
                            material_type_id BIGINT NOT NULL,
                            rate_per_gram NUMERIC(10,4) NOT NULL,
                            currency VARCHAR(3) NOT NULL DEFAULT 'INR',
                            unit VARCHAR(10) NOT NULL DEFAULT 'g',
                            source VARCHAR(50) NOT NULL,
                            fetched_at TIMESTAMP NOT NULL,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            created_by VARCHAR(255),
                            CONSTRAINT fk_metal_rate_material_type FOREIGN KEY (material_type_id) REFERENCES material_type(material_type_id) ON DELETE RESTRICT
);