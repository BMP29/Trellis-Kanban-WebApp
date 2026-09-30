ALTER TABLE users
    ADD COLUMN enabled BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN verification_code VARCHAR(255),
    ADD COLUMN verification_code_expires_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN created_at TIMESTAMP WITH TIME ZONE,
    ADD COLUMN last_update_at TIMESTAMP WITH TIME ZONE;

ALTER TABLE users
    ADD CONSTRAINT uk_users_username UNIQUE (username),
    ADD CONSTRAINT uk_users_email UNIQUE (email);
