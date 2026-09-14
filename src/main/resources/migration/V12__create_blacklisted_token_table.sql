DROP TABLE IF EXISTS blacklisted_tokens CASCADE;
DROP SEQUENCE IF EXISTS blacklisted_tokens_seq CASCADE;

CREATE SEQUENCE blacklisted_tokens_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1;

CREATE TABLE blacklisted_tokens (
                                    id BIGINT NOT NULL DEFAULT nextval('blacklisted_tokens_seq'),
                                    token_hash VARCHAR(255) NOT NULL,
                                    expiry_date TIMESTAMP NOT NULL,

                                    CONSTRAINT blacklisted_tokens_pkey PRIMARY KEY (id),
                                    CONSTRAINT unique_token_hash UNIQUE (token_hash)
);

ALTER SEQUENCE blacklisted_tokens_seq
    OWNED BY blacklisted_tokens.id;