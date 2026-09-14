CREATE TABLE offer_photo (
                             id SERIAL PRIMARY KEY,
                             img_url VARCHAR(500),
                             position Integer,
                             is_available BOOLEAN,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);