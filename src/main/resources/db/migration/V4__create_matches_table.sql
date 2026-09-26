CREATE TABLE matches (
                         id BIGSERIAL PRIMARY KEY,
                         user1_id BIGINT NOT NULL,
                         user2_id BIGINT NOT NULL,
                         created_at TIMESTAMP,
                         FOREIGN KEY (user1_id) REFERENCES users(id),
                         FOREIGN KEY (user2_id) REFERENCES users(id),
                         UNIQUE (user1_id, user2_id)
);