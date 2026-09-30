CREATE TABLE users (
                       id       uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                       username varchar(255) NOT NULL,
                       password varchar(255) NOT NULL,
                       email    varchar(255) NOT NULL,
                       pfp_path varchar(255) NOT NULL
);