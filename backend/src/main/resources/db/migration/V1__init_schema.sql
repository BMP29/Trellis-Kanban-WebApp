CREATE TABLE users (
                       id       uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                       username varchar(255) NOT NULL,
                       password varchar(255) NOT NULL,
                       email    varchar(255) NOT NULL,
                       pfp_path varchar(255) NOT NULL
);

CREATE TABLE boards (
                        id       uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                        title    varchar(255) NOT NULL,
                        owner_id uuid NOT NULL,
                        CONSTRAINT fk_boards_owner
                            FOREIGN KEY (owner_id) REFERENCES users (id)
);

CREATE INDEX idx_boards_owner ON boards (owner_id);

CREATE TABLE board_column (
                              id       uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                              board_id uuid NOT NULL,
                              tittle   varchar(255) NOT NULL,
                              position int NOT NULL CHECK (position >= 0),
                              CONSTRAINT fk_board_column_board
                                  FOREIGN KEY (board_id) REFERENCES boards (id) ON DELETE CASCADE
);

CREATE INDEX idx_board_column_board ON board_column (board_id);

CREATE TABLE board_members (
                               id       uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                               board_id uuid NOT NULL,
                               user_id  uuid NOT NULL,
                               role     varchar(50) NOT NULL,
                               CONSTRAINT fk_board_members_board
                                   FOREIGN KEY (board_id) REFERENCES boards (id) ON DELETE CASCADE,
                               CONSTRAINT fk_board_members_user
                                   FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE INDEX idx_board_members_board ON board_members (board_id);
CREATE INDEX idx_board_members_user  ON board_members (user_id);

CREATE TABLE task (
                      id          uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                      column_id   uuid NOT NULL,
                      position    int NOT NULL CHECK (position >= 0),
                      tittle      varchar(255) NOT NULL,
                      description varchar(2000) NOT NULL,
                      due_date    date NOT NULL,
                      priority    varchar(50) NOT NULL,
                      color_code  varchar(20) NOT NULL,
                      CONSTRAINT fk_task_column
                          FOREIGN KEY (column_id) REFERENCES board_column (id) ON DELETE CASCADE
);

CREATE INDEX idx_task_column ON task (column_id);

CREATE TABLE task_categories (
                                 task_id  uuid NOT NULL,
                                 category varchar(255),
                                 CONSTRAINT fk_task_categories_task
                                     FOREIGN KEY (task_id) REFERENCES task (id) ON DELETE CASCADE
);

CREATE INDEX idx_task_categories_task ON task_categories (task_id);