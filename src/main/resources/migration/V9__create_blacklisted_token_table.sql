CREATE SEQUENCE blacklisted_tokens_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1;

CREATE TABLE blacklisted_tokens (
                                    id BIGINT NOT NULL DEFAULT nextval('blacklisted_tokens_seq'),
                                    token_hash VARCHAR(255) NOT NULL UNIQUE,
                                    expiry_date TIMESTAMP NOT NULL,

                                    CONSTRAINT blacklisted_tokens_pkey PRIMARY KEY (id)
);

ALTER SEQUENCE blacklisted_tokens_seq
    OWNED BY blacklisted_tokens.id;