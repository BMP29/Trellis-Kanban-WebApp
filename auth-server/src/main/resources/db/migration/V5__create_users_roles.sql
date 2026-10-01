CREATE TABLE users_roles (
                             user_id UUID   NOT NULL REFERENCES users (id) ON DELETE CASCADE,
                             role_id BIGINT NOT NULL REFERENCES role (id),
                             PRIMARY KEY (user_id, role_id)
);