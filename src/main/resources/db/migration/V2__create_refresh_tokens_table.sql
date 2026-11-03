CREATE TABLE refresh_tokens (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    hash VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked_at TIMESTAMP
);

ALTER TABLE refresh_tokens
ADD CONSTRAINT fk_refresh_tokens_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE;

CREATE INDEX idx_refresh_tokens_user_id_revoked_created
ON refresh_tokens (user_id, revoked_at, created_at, id)
INCLUDE (expires_at);

CREATE INDEX idx_refresh_tokens_expires_at
ON refresh_tokens (expires_at);
