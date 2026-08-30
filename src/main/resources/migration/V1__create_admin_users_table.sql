DROP TABLE IF EXISTS admin_users CASCADE;

DROP SEQUENCE IF EXISTS admin_users_seq CASCADE;

CREATE SEQUENCE admin_users_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1;

CREATE TABLE admin_users (
                             id BIGINT NOT NULL DEFAULT nextval('admin_users_seq'),
                             email VARCHAR(255) NOT NULL,
                             otp VARCHAR(10),
                             otp_generated_time TIMESTAMP,
                             name VARCHAR(255),
                             phone_number VARCHAR(20),
                             image_url VARCHAR(500),
                             role VARCHAR(50),

                             CONSTRAINT admin_users_pkey PRIMARY KEY (id),
                             CONSTRAINT unique_email UNIQUE (email)
);

ALTER SEQUENCE admin_users_seq
    OWNED BY admin_users.id;