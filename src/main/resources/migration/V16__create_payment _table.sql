CREATE TABLE payment (
                         payment_id BIGSERIAL PRIMARY KEY,
                         order_id BIGINT NOT NULL,
                         razorpay_order_id VARCHAR(255),
                         razorpay_payment_id VARCHAR(255),
                         razorpay_signature VARCHAR(500),
                         amount NUMERIC(10,2),
                         status VARCHAR(30) NOT NULL,
                         payment_method VARCHAR(50),
                         failure_reason VARCHAR(500),
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         created_by VARCHAR(255),
                         updated_at TIMESTAMP,
                         updated_by VARCHAR(255),
                         CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES "order"(order_id) ON DELETE CASCADE
);