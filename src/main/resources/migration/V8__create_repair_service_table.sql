CREATE TABLE repair_service (
                                service_id BIGSERIAL PRIMARY KEY,
                                name VARCHAR(255) NOT NULL,
                                address VARCHAR(500),
                                status VARCHAR(50),
                                mobile_number VARCHAR(20),
                                notes TEXT,
                                created_by VARCHAR(255),
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_by VARCHAR(255),
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);