CREATE SEQUENCE privilege_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE role_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE privilege (
                           id   BIGINT NOT NULL PRIMARY KEY,
                           name VARCHAR(255)
);

CREATE TABLE role (
                      id   BIGINT NOT NULL PRIMARY KEY,
                      name VARCHAR(255)
);

CREATE TABLE roles_privileges (
                                  role_id      BIGINT NOT NULL REFERENCES role (id),
                                  privilege_id BIGINT NOT NULL REFERENCES privilege (id),
                                  PRIMARY KEY (role_id, privilege_id)
);