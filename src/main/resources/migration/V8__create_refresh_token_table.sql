-- =====================================================
-- Create Refresh Tokens Table
-- =====================================================

CREATE SEQUENCE refresh_tokens_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1;

CREATE TABLE refresh_tokens (
                                id BIGINT NOT NULL DEFAULT nextval('refresh_tokens_seq'),

                                email VARCHAR(255) NOT NULL,

                                role VARCHAR(255) NOT NULL,

                                client_type VARCHAR(20) NOT NULL DEFAULT 'WEB',

                                token_hash VARCHAR(64) NOT NULL,

                                issued_at TIMESTAMP NOT NULL,

                                expiry_date TIMESTAMP NOT NULL,

                                revoked BOOLEAN NOT NULL DEFAULT FALSE,

                                CONSTRAINT refresh_tokens_pkey
                                    PRIMARY KEY (id),

                                CONSTRAINT unique_refresh_token_hash
                                    UNIQUE (token_hash)
);

-- Sequence belongs to refresh_tokens.id
ALTER SEQUENCE refresh_tokens_seq
    OWNED BY refresh_tokens.id;

-- =====================================================
-- Index
-- =====================================================

CREATE INDEX idx_email_role_client
    ON refresh_tokens(email, role, client_type);