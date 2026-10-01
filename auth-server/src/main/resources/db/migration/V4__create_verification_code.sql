CREATE TABLE verification_code (
                                   id          UUID         NOT NULL PRIMARY KEY,
                                   user_id     UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
                                   type        VARCHAR(255) NOT NULL,
                                   code_hash   VARCHAR(255) NOT NULL,
                                   expires_at  TIMESTAMPTZ  NOT NULL,
                                   consumed_at TIMESTAMPTZ,

                                   CONSTRAINT verification_code_type_check CHECK (
                                       type IN (
                                                'ACCOUNT_VERIFICATION',
                                                'ACCOUNT_DELETION',
                                                'EMAIL_CHANGE',
                                                'USERNAME_CHANGE',
                                                'PASSWORD_CHANGE'
                                           )
                                       )
);

CREATE INDEX idx_verification_code_user_type
    ON verification_code (user_id, type);
