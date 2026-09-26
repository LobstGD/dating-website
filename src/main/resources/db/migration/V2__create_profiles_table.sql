CREATE TABLE profiles (
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT NOT NULL UNIQUE,
                          firstname VARCHAR(255),
                          lastname VARCHAR(255),
                          age INTEGER,
                          gender VARCHAR(50),
                          city VARCHAR(255),
                          bio TEXT,
                          updated_at TIMESTAMP,
                          FOREIGN KEY (user_id) REFERENCES users(id)
);