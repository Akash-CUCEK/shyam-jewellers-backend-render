CREATE TABLE admin_users (
                             id BIGSERIAL PRIMARY KEY,
                             email VARCHAR(255) NOT NULL,
                             password VARCHAR(255) NOT NULL,
                             otp VARCHAR(10),
                             otp_generated_time TIMESTAMP,
                             name VARCHAR(255),
                             phone_number VARCHAR(20),
                             image_url VARCHAR(500),
                             role VARCHAR(50),
                             CONSTRAINT unique_email UNIQUE (email)
);