### Tournament ###
CREATE TABLE tournament (
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    title               VARCHAR(30) NOT NULL,
    contents            VARCHAR(1000) NOT NULL,
    start_time          DATETIME NOT NULL,
    end_time            DATETIME NOT NULL,
    type                VARCHAR(15) NOT NULL,
    status              VARCHAR(10) NOT NULL DEFAULT 'BEFORE',
    created_at          DATETIME NOT NULL,
    modified_at         DATETIME NOT NULL,
    winner_id           BIGINT,
    PRIMARY KEY (id),
    FOREIGN KEY (winner_id) REFERENCES user(id)
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
    FOREIGN KEY (tournament_id) REFERENCES tournament(id),
    FOREIGN KEY (user_id)       REFERENCES user(id)
);

### TournamentGame ###
CREATE TABLE tournament_game (
    id                  BIGINT NOT NULL AUTO_INCREMENT,
    tournament_id       BIGINT NOT NULL,
    game_id             BIGINT,
    round               VARCHAR(20) NOT NULL,
    created_at          DATETIME NOT NULL,
    modified_at          DATETIME NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (tournament_id) REFERENCES tournament(id),
    FOREIGN KEY (game_id)       REFERENCES game(id)
);
