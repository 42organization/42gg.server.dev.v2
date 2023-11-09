### Tournament ###
CREATE TABLE tournament (
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    title               VARCHAR(20) NOT NULL,
    contents            VARCHAR(1000) NOT NULL,
    start_time          DATETIME NOT NULL,
    end_time            DATETIME NOT NULL,
    type                VARCHAR(15) NOT NULL,
    status              VARCHAR(10) NOT NULL DEFAULT 'BEFORE',
    created_at          DATETIME NOT NULL,
    modified_at          DATETIME NOT NULL,
    PRIMARY KEY (id)
);

### TournamentUser ###
CREATE TABLE tournament_user (
    id              BIGINT NOT NULL AUTO_INCREMENT,
    user_id         BIGINT NOT NULL,
    tournament_id   BIGINT NOT NULL,
    is_joined       BOOLEAN NOT NULL DEFAULT FALSE,
    register_time   DATETIME NOT NULL,
    created_at      DATETIME NOT NULL,
    modified_at      DATETIME NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (tournament_id) REFERENCES tournament(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE ,
    FOREIGN KEY (user_id) REFERENCES user(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

### TournamentGame ###
CREATE TABLE tournament_game (
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    tournament_id       BIGINT NOT NULL,
    game_id             BIGINT NOT NULL,
    round               VARCHAR(10) NOT NULL,
    created_at          DATETIME NOT NULL,
    modified_at          DATETIME NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (tournament_id) REFERENCES tournament(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    FOREIGN KEY (game_id) REFERENCES game(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
