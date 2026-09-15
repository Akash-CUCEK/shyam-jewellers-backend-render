CREATE TABLE address (
                         address_id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         address_label VARCHAR(100),
                         address_line1 VARCHAR(500),
                         address_line2 VARCHAR(500),
                         city VARCHAR(255),
                         state VARCHAR(255),
                         pincode VARCHAR(20),
                         phone_number VARCHAR(20),
                         is_default BOOLEAN NOT NULL DEFAULT FALSE,
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         created_by VARCHAR(255),
                         updated_at TIMESTAMP,
                         updated_by VARCHAR(255),
                         CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);