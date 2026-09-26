CREATE TABLE likes (
                       id BIGSERIAL PRIMARY KEY,
                       from_user_id BIGINT NOT NULL,
                       to_user_id BIGINT NOT NULL,
                       created_at TIMESTAMP,
                       FOREIGN KEY (from_user_id) REFERENCES users(id),
                       FOREIGN KEY (to_user_id) REFERENCES users(id),
                       UNIQUE (from_user_id, to_user_id)
);