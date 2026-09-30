CREATE TABLE order_item (
                            order_item_id BIGSERIAL PRIMARY KEY,
                            order_id BIGINT NOT NULL,
                            product_variant_id BIGINT,
                            quantity INTEGER NOT NULL DEFAULT 1,
                            sku_code_snapshot VARCHAR(255),
                            product_name_snapshot VARCHAR(255),
                            weight_snapshot NUMERIC(10,3),
                            metal_value_snapshot NUMERIC(10,2),
                            making_charge_snapshot NUMERIC(10,2),
                            gst_snapshot NUMERIC(10,2),
                            final_price_snapshot NUMERIC(10,2),
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            created_by VARCHAR(255),
                            updated_at TIMESTAMP,
                            updated_by VARCHAR(255),
                            CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES "order"(order_id) ON DELETE CASCADE,
                            CONSTRAINT fk_order_item_variant FOREIGN KEY (product_variant_id) REFERENCES product_variant(variant_id) ON DELETE SET NULL
);