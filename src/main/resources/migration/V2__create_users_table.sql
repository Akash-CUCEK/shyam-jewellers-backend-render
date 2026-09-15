CREATE TABLE users (
                       user_id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) UNIQUE,
                       otp VARCHAR(255),
                       otp_generated_time TIMESTAMP
);