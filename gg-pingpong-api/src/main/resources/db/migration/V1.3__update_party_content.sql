CREATE TABLE room (
    room_id BIGINT AUTO_INCREMENT,
    host_id BIGINT,
    creator_id BIGINT,
    category_id BIGINT,
    title VARCHAR(15),
    content VARCHAR(100),
    max_people INT,
    min_people INT,
    due_date TIMESTAMP,
    create_date TIMESTAMP,
    room_status VARCHAR(10),
    PRIMARY KEY(room_id)
);

CREATE TABLE Category (
    category_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(10) NOT NULL
);
