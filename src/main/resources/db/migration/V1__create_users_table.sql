CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50),
                       is_active BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP
);