CREATE TABLE product_variant (
    variant_id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    sku_code VARCHAR(255) NOT NULL UNIQUE,
    weight NUMERIC(10, 3) NOT NULL,
    size VARCHAR(100),
    quantity INTEGER NOT NULL DEFAULT 0,
    reserved_quantity INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    CONSTRAINT fk_product_variant_product FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE
);