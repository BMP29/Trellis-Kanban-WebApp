ALTER TABLE users
    ALTER COLUMN password DROP NOT NULL,
    ALTER COLUMN pfp_path DROP NOT NULL;

ALTER TABLE users
    ADD COLUMN provider    VARCHAR(50),
    ADD COLUMN provider_id VARCHAR(255);

CREATE UNIQUE INDEX uk_users_provider_provider_id
    ON users (provider, provider_id)
    WHERE provider_id IS NOT NULL;