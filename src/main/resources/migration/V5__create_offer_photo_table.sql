CREATE TABLE offer_photo (
                             id SERIAL PRIMARY KEY,
                             img_url1 VARCHAR(500),
                             img_url2 VARCHAR(500),
                             img_url3 VARCHAR(500),
                             img_url4 VARCHAR(500),
                             img_url5 VARCHAR(500),
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);