DROP TABLE IF EXISTS refresh_tokens CASCADE;
DROP SEQUENCE IF EXISTS refresh_tokens_seq CASCADE;

CREATE SEQUENCE refresh_tokens_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1;

CREATE TABLE refresh_tokens (
                                id BIGINT NOT NULL DEFAULT nextval('refresh_tokens_seq'),
                                email VARCHAR(255) NOT NULL,
                                role VARCHAR(255) NOT NULL,
                                token_hash VARCHAR(255) NOT NULL,
                                expiry_date TIMESTAMP NOT NULL,

                                CONSTRAINT refresh_tokens_pkey PRIMARY KEY (id),
                                CONSTRAINT unique_token_hash UNIQUE (token_hash)
);

ALTER SEQUENCE refresh_tokens_seq
    OWNED BY refresh_tokens.id;