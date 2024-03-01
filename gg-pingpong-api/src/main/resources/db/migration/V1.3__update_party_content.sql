CREATE TABLE category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(10) NOT NULL,
    created_at DATETIME,
    modified_at DATETIME
);

CREATE TABLE room (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    host_id BIGINT,
    creator_id BIGINT,
    category_id BIGINT,
    title VARCHAR(15),
    content VARCHAR(100),
    max_people INT,
    min_people INT,
    due_date DATETIME,
    created_at DATETIME,
    modified_at DATETIME,
    room_status VARCHAR(10),
    FOREIGN KEY (host_id) REFERENCES user(id),
    FOREIGN KEY (creator_id) REFERENCES user(id),
    FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE user_room (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    room_id BIGINT,
    nickname VARCHAR(20),
    is_exist BOOLEAN,
    created_at DATETIME,
    modified_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (room_id) REFERENCES room(id)
);

CREATE TABLE comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT,
    userroom_id BIGINT,
    room_id BIGINT,
    content VARCHAR(100),
    is_hidden BOOLEAN,
    created_at DATETIME,
    modified_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES user(id),
    FOREIGN KEY (userroom_id) REFERENCES userroom(id),
    FOREIGN KEY (room_id) REFERENCES room(id)
);

CREATE TABLE comment_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reporter_id BIGINT,
    comment_id BIGINT,
    room_id BIGINT,
    message VARCHAR(100),
    created_at DATETIME,
    modified_at DATETIME,
    FOREIGN KEY (reporter_id) REFERENCES user(id),
    FOREIGN KEY (comment_id) REFERENCES comment(id),
    FOREIGN KEY (room_id) REFERENCES room(id)
);

CREATE TABLE game_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_id BIGINT,
    game_name VARCHAR(10),
    max_game_people INT,
    min_game_people INT,
    max_game_time INT,
    min_game_time INT,
    genre VARCHAR(10),
    difficulty VARCHAR(10),
    summary VARCHAR(100),
    created_at DATETIME,
    modified_at DATETIME,
    FOREIGN KEY (category_id) REFERENCES category(id)
);

CREATE TABLE room_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reporter_id BIGINT,
    reportee_id BIGINT,
    room_id BIGINT,
    message VARCHAR(100),
    created_at DATETIME,
    modified_at DATETIME,
    FOREIGN KEY (reporter_id) REFERENCES user(id),
    FOREIGN KEY (reportee_id) REFERENCES user(id),
    FOREIGN KEY (room_id) REFERENCES room(id)
);

CREATE TABLE user_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    reporter_id BIGINT,
    reportee_id BIGINT,
    room_id BIGINT,
    created_at DATETIME,
    modified_at DATETIME,
    message VARCHAR(100),
    FOREIGN KEY (reporter_id) REFERENCES user(id),
    FOREIGN KEY (reportee_id) REFERENCES user(id),
    FOREIGN KEY (room_id) REFERENCES room(id)
);
