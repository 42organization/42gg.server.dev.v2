CREATE TABLE `agenda`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `key`           BINARY(16) NOT NULL,
    `title`         VARCHAR(50)  NOT NULL,
    `content`       VARCHAR(500) NOT NULL,
    `deadline`      DATETIME     NOT NULL,
    `start_time`    DATETIME     NOT NULL,
    `end_time`      DATETIME     NOT NULL,
    `min_team`      INT          NOT NULL,
    `max_team`      INT          NOT NULL,
    `current_team`  INT          NOT NULL,
    `min_people`    INT          NOT NULL,
    `max_people`    INT          NOT NULL,
    `poster_uri`    VARCHAR(255) NULL,
    `host_intra_id` VARCHAR(30)  NOT NULL,
    `location`      VARCHAR(30)  NOT NULL,
    `status`        VARCHAR(10)  NOT NULL,
    `is_official`   BOOL         NOT NULL,
    `is_ranking`    BOOL         NOT NULL,
    `created_at`    DATETIME     NOT NULL,
    `modified_at`   DATETIME     NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_agenda_key` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `agenda_team`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT,
    `agenda_id`       BIGINT       NOT NULL,
    `key`             BINARY(16) NOT NULL,
    `name`            VARCHAR(30)  NOT NULL,
    `content`         VARCHAR(500) NOT NULL,
    `leader_intra_id` VARCHAR(30)  NOT NULL,
    `status`          VARCHAR(10)  NOT NULL,
    `location`        VARCHAR(10)  NOT NULL,
    `mate_count`      INT          NOT NULL,
    `award`           VARCHAR(30)  NOT NULL,
    `award_priority`  INT          NOT NULL,
    `is_private`      BOOL         NOT NULL,
    `created_at`      DATETIME     NOT NULL,
    `modified_at`     DATETIME     NOT NULL,
    PRIMARY KEY (`id`),
    KEY               `fk_agenda_team_agenda_agenda_id` (`agenda_id`),
    CONSTRAINT `fk_agenda_team_agenda_agenda_id` FOREIGN KEY (`agenda_id`) REFERENCES `agenda` (`id`),
    UNIQUE KEY `uk_agenda_team_key` (`key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;;

CREATE TABLE `agenda_announcement`
(
    `id`          BIGINT        NOT NULL AUTO_INCREMENT,
    `agenda_id`   BIGINT        NOT NULL,
    `title`       VARCHAR(50)   NOT NULL,
    `content`     VARCHAR(1000) NOT NULL,
    `is_show`     BOOL          NOT NULL,
    `created_at`  DATETIME      NOT NULL,
    `modified_at` DATETIME      NOT NULL,
    PRIMARY KEY (`id`),
    KEY           `fk_agenda_announcement_agenda_agenda_id` (`agenda_id`),
    CONSTRAINT `fk_agenda_announcement_agenda_agenda_id` FOREIGN KEY (`agenda_id`) REFERENCES `agenda` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;;

CREATE TABLE `agenda_profile`
(
    `id`          BIGINT        NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT        NOT NULL,
    `content`     VARCHAR(1000) NOT NULL,
    `github_url`  VARCHAR(255)  NOT NULL,
    `coalition`   VARCHAR(30)   NOT NULL,
    `location`    VARCHAR(30)   NOT NULL,
    `created_at`  DATETIME      NOT NULL,
    `modified_at` DATETIME      NOT NULL,
    PRIMARY KEY (`id`),
    KEY           `fk_agenda_profile_user_user_id` (`user_id`),
    CONSTRAINT `fk_agenda_profile_user_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;;

CREATE TABLE `agenda_team_profile`
(
    `id`             BIGINT   NOT NULL AUTO_INCREMENT,
    `profile_id`     BIGINT   NOT NULL,
    `agenda_team_id` BIGINT   NOT NULL,
    `is_exist`       BOOL     NOT NULL,
    `created_at`     DATETIME NOT NULL,
    `modified_at`    DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    KEY              `fk_agenda_team_profile_profile_profile_id` (`profile_id`),
    KEY              `fk_agenda_team_profile_agenda_team_agenda_team_id` (`agenda_team_id`),
    CONSTRAINT `fk_agenda_team_profile_profile_profile_id` FOREIGN KEY (`profile_id`) REFERENCES `agenda_profile` (`id`),
    CONSTRAINT `fk_agenda_team_profile_agenda_team_agenda_team_id` FOREIGN KEY (`agenda_team_id`) REFERENCES `agenda_team` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;;

CREATE TABLE `ticket`
(
    `id`          BIGINT   NOT NULL AUTO_INCREMENT,
    `profile_id`  BIGINT   NOT NULL,
    `is_used`     BOOL     NOT NULL,
    `is_approve`  BOOL     NOT NULL,
    `created_at`  DATETIME NOT NULL,
    `modified_at` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    KEY           `fk_ticket_profile_profile_id` (`profile_id`),
    CONSTRAINT `fk_ticket_profile_profile_id` FOREIGN KEY (`profile_id`) REFERENCES `agenda_profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai
