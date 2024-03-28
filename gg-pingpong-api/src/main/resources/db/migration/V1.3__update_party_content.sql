CREATE TABLE category
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(10) NOT NULL,
    created_at  DATETIME    NOT NULL,
    modified_at DATETIME    NOT NULL
);

CREATE TABLE room
(
    id             BIGINT PRIMARY KEY AUTO_INCREMENT,
    host_id        BIGINT       NOT NULL,
    creator_id     BIGINT       NOT NULL,
    category_id    BIGINT       NOT NULL,
    title          VARCHAR(15)  NOT NULL,
    content        VARCHAR(100) NOT NULL,
    current_people INT          NOT NULL,
    max_people     INT          NOT NULL,
    min_people     INT          NOT NULL DEFAULT 2,
    due_date       DATETIME     NOT NULL,
    start_date     DATETIME,
    created_at     DATETIME     NOT NULL,
    modified_at    DATETIME     NOT NULL,
    status         VARCHAR(10)  NOT NULL,
    FOREIGN KEY (host_id) REFERENCES user (id),
    FOREIGN KEY (creator_id) REFERENCES user (id),
    FOREIGN KEY (category_id) REFERENCES category (id)
);

CREATE TABLE user_room
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT      NOT NULL,
    room_id     BIGINT      NOT NULL,
    nickname    VARCHAR(20) NOT NULL,
    is_exist    BOOLEAN     NOT NULL,
    created_at  DATETIME    NOT NULL,
    modified_at DATETIME    NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (room_id) REFERENCES room (id)
);

CREATE TABLE comment
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id      BIGINT       NOT NULL,
    user_room_id BIGINT       NOT NULL,
    room_id      BIGINT       NOT NULL,
    content      VARCHAR(100) NOT NULL,
    is_hidden    BOOLEAN      NOT NULL,
    created_at   DATETIME     NOT NULL,
    modified_at  DATETIME     NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user (id),
    FOREIGN KEY (user_room_id) REFERENCES user_room (id),
    FOREIGN KEY (room_id) REFERENCES room (id)
);

CREATE TABLE comment_report
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id     BIGINT       NOT NULL,
    reporter_id BIGINT       NOT NULL,
    comment_id  BIGINT       NOT NULL,
    message     VARCHAR(100) NOT NULL,
    created_at  DATETIME     NOT NULL,
    modified_at DATETIME     NOT NULL,
    FOREIGN KEY (reporter_id) REFERENCES user (id),
    FOREIGN KEY (comment_id) REFERENCES comment (id),
    FOREIGN KEY (room_id) REFERENCES room (id)
);

CREATE TABLE game_template
(
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id     BIGINT       NOT NULL,
    game_name       VARCHAR(20)  NOT NULL,
    max_game_people INT          NOT NULL,
    min_game_people INT          NOT NULL,
    max_game_time   INT          NOT NULL,
    min_game_time   INT          NOT NULL,
    genre           VARCHAR(10),
    difficulty      VARCHAR(10),
    summary         VARCHAR(100) NOT NULL,
    created_at      DATETIME     NOT NULL,
    modified_at     DATETIME     NOT NULL,
    FOREIGN KEY (category_id) REFERENCES category (id)
);

CREATE TABLE room_report
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    reporter_id BIGINT       NOT NULL,
    reportee_id BIGINT       NOT NULL,
    room_id     BIGINT       NOT NULL,
    message     VARCHAR(100) NOT NULL,
    created_at  DATETIME     NOT NULL,
    modified_at DATETIME     NOT NULL,
    FOREIGN KEY (reporter_id) REFERENCES user (id),
    FOREIGN KEY (reportee_id) REFERENCES user (id),
    FOREIGN KEY (room_id) REFERENCES room (id)
);

CREATE TABLE user_report
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    reporter_id BIGINT       NOT NULL,
    reportee_id BIGINT       NOT NULL,
    room_id     BIGINT       NOT NULL,
    created_at  DATETIME     NOT NULL,
    modified_at DATETIME     NOT NULL,
    message     VARCHAR(100) NOT NULL,
    FOREIGN KEY (reporter_id) REFERENCES user (id),
    FOREIGN KEY (reportee_id) REFERENCES user (id),
    FOREIGN KEY (room_id) REFERENCES room (id)
);

CREATE TABLE party_penalty (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    penalty_type VARCHAR(20) NOT NULL,
    message     VARCHAR(100) NOT NULL,
    start_time  DATETIME     NOT NULL,
    penalty_time INT         NOT NULL,
    created_at  DATETIME     NOT NULL,
    modified_at DATETIME     NOT NULL,
    FOREIGN KEY (user_id) REFERENCES user(id)
);
