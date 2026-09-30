CREATE TABLE purity (
                        purity_id BIGSERIAL PRIMARY KEY,
                        material_type_id BIGINT NOT NULL,
                        purity_name VARCHAR(255) NOT NULL,
                        purity_factor NUMERIC(10, 6) NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        created_by VARCHAR(255),
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_by VARCHAR(255),
                        status BOOLEAN NOT NULL DEFAULT TRUE,
                        CONSTRAINT fk_purity_material_type FOREIGN KEY (material_type_id) REFERENCES material_type(material_type_id) ON DELETE RESTRICT,
                        CONSTRAINT uk_purity_material_type_purity_name UNIQUE (material_type_id, purity_name)
);