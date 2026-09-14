CREATE TABLE product_tag (
    product_tag_id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255),
    CONSTRAINT fk_product_tag_product FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE CASCADE,
    CONSTRAINT fk_product_tag_tag FOREIGN KEY (tag_id) REFERENCES tag(tag_id) ON DELETE RESTRICT,
    CONSTRAINT uk_product_tag_product_tag UNIQUE (product_id, tag_id)
);