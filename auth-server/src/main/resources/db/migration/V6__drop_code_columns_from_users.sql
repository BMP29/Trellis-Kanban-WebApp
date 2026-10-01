ALTER TABLE users
    DROP COLUMN IF EXISTS verification_code,
    DROP COLUMN IF EXISTS verification_code_expires_at;