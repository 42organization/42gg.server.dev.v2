CREATE TABLE `agenda`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `agenda_key`    BINARY(16)   NOT NULL,
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
    `is_official`   BIT(1)       NOT NULL,
    `is_ranking`    BIT(1)       NOT NULL,
    `created_at`    DATETIME     NOT NULL,
    `modified_at`   DATETIME     NOT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_agenda_agenda_key` (`agenda_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `agenda_team`
(
    `id`              BIGINT       NOT NULL AUTO_INCREMENT,
    `agenda_id`       BIGINT       NOT NULL,
    `team_key`        BINARY(16)   NOT NULL,
    `name`            VARCHAR(30)  NOT NULL,
    `content`         VARCHAR(500) NOT NULL,
    `leader_intra_id` VARCHAR(30)  NOT NULL,
    `status`          VARCHAR(10)  NOT NULL,
    `location`        VARCHAR(10)  NOT NULL,
    `mate_count`      INT          NOT NULL,
    `award`           VARCHAR(30)  NOT NULL,
    `award_priority`  INT          NOT NULL,
    `is_private`      BIT(1)       NOT NULL,
    `created_at`      DATETIME     NOT NULL,
    `modified_at`     DATETIME     NOT NULL,
    PRIMARY KEY (`id`),
    KEY               `fk_agenda_team_agenda_agenda_id` (`agenda_id`),
    CONSTRAINT `fk_agenda_team_agenda_agenda_id` FOREIGN KEY (`agenda_id`) REFERENCES `agenda` (`id`),
    UNIQUE KEY `uk_agenda_team_team_key` (`team_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `agenda_announcement`
(
    `id`          BIGINT        NOT NULL AUTO_INCREMENT,
    `agenda_id`   BIGINT        NOT NULL,
    `title`       VARCHAR(50)   NOT NULL,
    `content`     VARCHAR(1000) NOT NULL,
    `is_show`     BIT(1)        NOT NULL,
    `created_at`  DATETIME      NOT NULL,
    `modified_at` DATETIME      NOT NULL,
    PRIMARY KEY (`id`),
    KEY           `fk_agenda_announcement_agenda_agenda_id` (`agenda_id`),
    CONSTRAINT `fk_agenda_announcement_agenda_agenda_id` FOREIGN KEY (`agenda_id`) REFERENCES `agenda` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `agenda_profile`
(
    `id`          BIGINT        NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT        NOT NULL,
    `forty_two_id`BIGINT        NOT NULL,
    `intra_id`    VARCHAR(30)   NOT NULL,
    `content`     VARCHAR(1000) NOT NULL,
    `github_url`  VARCHAR(255)  NULL,
    `coalition`   VARCHAR(30)   NOT NULL,
    `location`    VARCHAR(30)   NOT NULL,
    `created_at`  DATETIME      NOT NULL,
    `modified_at` DATETIME      NOT NULL,
    PRIMARY KEY (`id`),
    KEY           `fk_agenda_profile_user_user_id` (`user_id`),
    CONSTRAINT `fk_agenda_profile_user_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `agenda_team_profile`
(
    `id`             BIGINT   NOT NULL AUTO_INCREMENT,
    `profile_id`     BIGINT   NOT NULL,
    `agenda_id`      BIGINT   NOT NULL,
    `agenda_team_id` BIGINT   NOT NULL,
    `is_exist`       BIT(1)   NOT NULL,
    `created_at`     DATETIME NOT NULL,
    `modified_at`    DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    KEY              `fk_agenda_team_profile_profile_profile_id` (`profile_id`),
    KEY              `fk_agenda_team_profile_agenda_agenda_id` (`agenda_id`),
    KEY              `fk_agenda_team_profile_agenda_team_agenda_team_id` (`agenda_team_id`),
    CONSTRAINT `fk_agenda_team_profile_profile_profile_id` FOREIGN KEY (`profile_id`) REFERENCES `agenda_profile` (`id`),
    CONSTRAINT `fk_agenda_team_profile_agenda_team_agenda_team_id` FOREIGN KEY (`agenda_team_id`) REFERENCES `agenda_team` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `ticket`
(
    `id`          BIGINT   NOT NULL AUTO_INCREMENT,
    `profile_id`  BIGINT   NOT NULL,
    `issued_from` BINARY(16) NULL,
    `used_to`     BINARY(16) NULL,
	`is_approved` BOOLEAN NOT NULL,
    `approved_at` DATETIME NULL,
    `is_used`     BOOLEAN NOT NULL,
    `used_at`     DATETIME NULL,
    `created_at`  DATETIME NOT NULL,
    `modified_at` DATETIME NOT NULL,
    PRIMARY KEY (`id`),
    KEY           `fk_ticket_profile_profile_id` (`profile_id`),
    CONSTRAINT `fk_ticket_profile_profile_id` FOREIGN KEY (`profile_id`) REFERENCES `agenda_profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `agenda_poster_image`
(
    `id`          BIGINT         NOT NULL AUTO_INCREMENT,
    `agenda_id`   BIGINT         NOT NULL,
    `image_uri`   VARCHAR(255)   NOT NULL,
    `is_current`  BOOLEAN        NOT NULL,
    `s3_deleted`  BOOLEAN        NOT NULL,
    `created_at`  DATETIME       NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
