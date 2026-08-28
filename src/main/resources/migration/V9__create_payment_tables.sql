CREATE SEQUENCE IF NOT EXISTS payments_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS payment_transactions_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS payment_gateway_responses_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS payment_audits_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT PRIMARY KEY DEFAULT nextval('payments_seq'),
    payment_reference VARCHAR(80) NOT NULL,
    order_id BIGINT NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    status VARCHAR(40) NOT NULL,
    payment_method VARCHAR(40) NOT NULL,
    gateway VARCHAR(40) NOT NULL,
    idempotency_key VARCHAR(140),
    gateway_order_id VARCHAR(140),
    gateway_payment_id VARCHAR(140),
    gateway_reference_id VARCHAR(140),
    payment_url VARCHAR(1000),
    failure_code VARCHAR(100),
    failure_reason VARCHAR(1000),
    expires_at TIMESTAMP,
    paid_at TIMESTAMP,
    failed_at TIMESTAMP,
    cancelled_at TIMESTAMP,
    refunded_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT,
    CONSTRAINT fk_payments_order FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT uk_payments_reference UNIQUE (payment_reference),
    CONSTRAINT uk_payments_idempotency_key UNIQUE (idempotency_key)
);

CREATE INDEX IF NOT EXISTS idx_payments_order_id ON payments(order_id);
CREATE INDEX IF NOT EXISTS idx_payments_status ON payments(status);
CREATE INDEX IF NOT EXISTS idx_payments_gateway_order_id ON payments(gateway_order_id);

CREATE TABLE IF NOT EXISTS payment_transactions (
    id BIGINT PRIMARY KEY DEFAULT nextval('payment_transactions_seq'),
    payment_id BIGINT NOT NULL,
    transaction_type VARCHAR(40) NOT NULL,
    status VARCHAR(40) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    gateway_order_id VARCHAR(140),
    gateway_transaction_id VARCHAR(140),
    idempotency_key VARCHAR(140),
    signature_verified BOOLEAN,
    gateway_payload TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_transactions_payment FOREIGN KEY (payment_id) REFERENCES payments(id)
);

CREATE INDEX IF NOT EXISTS idx_payment_transactions_payment_id
    ON payment_transactions(payment_id);
CREATE INDEX IF NOT EXISTS idx_payment_transactions_gateway_txn
    ON payment_transactions(gateway_transaction_id);

CREATE TABLE IF NOT EXISTS payment_gateway_responses (
    id BIGINT PRIMARY KEY DEFAULT nextval('payment_gateway_responses_seq'),
    payment_id BIGINT NOT NULL,
    gateway VARCHAR(40) NOT NULL,
    event_type VARCHAR(80) NOT NULL,
    status VARCHAR(40),
    gateway_order_id VARCHAR(140),
    gateway_payment_id VARCHAR(140),
    signature VARCHAR(500),
    signature_valid BOOLEAN,
    raw_payload TEXT,
    received_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_gateway_responses_payment FOREIGN KEY (payment_id) REFERENCES payments(id)
);

CREATE INDEX IF NOT EXISTS idx_payment_gateway_responses_payment_id
    ON payment_gateway_responses(payment_id);
CREATE INDEX IF NOT EXISTS idx_payment_gateway_responses_gateway_order
    ON payment_gateway_responses(gateway_order_id);

CREATE TABLE IF NOT EXISTS payment_audits (
    id BIGINT PRIMARY KEY DEFAULT nextval('payment_audits_seq'),
    payment_id BIGINT NOT NULL,
    action VARCHAR(80) NOT NULL,
    from_status VARCHAR(40),
    to_status VARCHAR(40),
    actor VARCHAR(120),
    message VARCHAR(1000),
    metadata TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_audits_payment FOREIGN KEY (payment_id) REFERENCES payments(id)
);

CREATE INDEX IF NOT EXISTS idx_payment_audits_payment_id ON payment_audits(payment_id);
CREATE INDEX IF NOT EXISTS idx_payment_audits_created_at ON payment_audits(created_at);
