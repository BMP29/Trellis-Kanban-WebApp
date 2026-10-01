CREATE INDEX IF NOT EXISTS idx_verification_code_expires_at
    ON verification_code (expires_at);

CREATE EXTENSION IF NOT EXISTS pg_cron;

SELECT cron.schedule(
               'cleanup-expired-verification-codes',
               '0 * * * *',
               $$DELETE FROM verification_code WHERE expires_at < NOW();$$
       );