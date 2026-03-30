CREATE TABLE category (
                          category_id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          show_on_home BOOLEAN NOT NULL DEFAULT FALSE,
                          image_url VARCHAR(500),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          created_by VARCHAR(255),
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_by VARCHAR(255),
                          status BOOLEAN NOT NULL
);