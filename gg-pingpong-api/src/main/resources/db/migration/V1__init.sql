CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `created_at` datetime NOT NULL,
                        `modified_at` datetime DEFAULT NULL,
                        `e_mail` varchar(60) DEFAULT NULL,
                        `image_uri` varchar(255) DEFAULT NULL,
                        `intra_id` varchar(30) NOT NULL,
                        `total_exp` int DEFAULT NULL,
                        `sns_noti_opt` varchar(10) DEFAULT NULL,
                        `racket_type` varchar(10) DEFAULT NULL,
                        `role_type` varchar(10) NOT NULL,
                        `kakao_id` bigint DEFAULT NULL,
                        `background` varchar(255) DEFAULT 'BASIC',
                        `edge` varchar(255) DEFAULT 'BASIC',
                        `gg_coin` int DEFAULT '0',
                        `text_color` varchar(10) DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `UK_l5220ph2ndjh75g6ya39wy519` (`intra_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `season` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `end_time` datetime NOT NULL,
                          `ppp_gap` int NOT NULL,
                          `season_name` varchar(20) NOT NULL,
                          `start_ppp` int NOT NULL,
                          `start_time` datetime NOT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `tier` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `image_uri` varchar(255) DEFAULT NULL,
                        `name` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `announcement` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `content` varchar(1000) NOT NULL,
                                `creator_intra_id` varchar(30) NOT NULL,
                                `created_at` datetime NOT NULL,
                                `deleter_intra_id` varchar(30) DEFAULT NULL,
                                `deleted_at` datetime DEFAULT NULL,
                                `modified_at` datetime DEFAULT NULL,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `coin_history` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `amount` int DEFAULT NULL,
                                `created_at` datetime(6) NOT NULL,
                                `history` varchar(30) DEFAULT NULL,
                                `user_id` bigint DEFAULT NULL,
                                PRIMARY KEY (`id`),
                                KEY `user_id` (`user_id`),
                                CONSTRAINT `coin_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `coin_policy` (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `user_id` bigint DEFAULT NULL,
                               `attendance` int DEFAULT NULL,
                               `created_at` datetime(6) NOT NULL,
                               `normal` int DEFAULT NULL,
                               `rank_lose` int DEFAULT NULL,
                               `rank_win` int DEFAULT NULL,
                               PRIMARY KEY (`id`),
                               KEY `fk_coin_policy_user_user_id` (`user_id`),
                               CONSTRAINT `fk_coin_policy_user_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `feedback` (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `created_at` datetime NOT NULL,
                            `modified_at` datetime DEFAULT NULL,
                            `content` varchar(600) NOT NULL,
                            `is_solved` bit(1) NOT NULL,
                            `user_id` bigint DEFAULT NULL,
                            `category` varchar(15) NOT NULL,
                            PRIMARY KEY (`id`),
                            KEY `fk_feedback_user_user_id` (`user_id`),
                            CONSTRAINT `fk_feedback_user_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `game` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `season_id` bigint NOT NULL,
                        `start_time` datetime NOT NULL,
                        `end_time` datetime DEFAULT NULL,
                        `status` varchar(10) NOT NULL,
                        `mode` varchar(10) NOT NULL,
                        PRIMARY KEY (`id`),
                        KEY `fk_game_season_season_id` (`season_id`),
                        CONSTRAINT `fk_game_season_season_id` FOREIGN KEY (`season_id`) REFERENCES `season` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `item` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `created_at` datetime(6) NOT NULL,
                        `discount` int DEFAULT NULL,
                        `image_uri` varchar(255) DEFAULT NULL,
                        `is_visible` bit(1) NOT NULL,
                        `name` varchar(30) DEFAULT NULL,
                        `price` int NOT NULL,
                        `creator_intra_id` varchar(10) NOT NULL,
                        `deleter_intra_id` varchar(10) DEFAULT NULL,
                        `type` varchar(255) NOT NULL,
                        `main_content` varchar(255) DEFAULT NULL,
                        `sub_content` varchar(255) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `receipt` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `owner_intra_id` varchar(255) NOT NULL,
                           `created_at` datetime(6) NOT NULL,
                           `purchaser_intra_id` varchar(255) NOT NULL,
                           `status` varchar(255) NOT NULL,
                           `item_id` bigint NOT NULL,
                           PRIMARY KEY (`id`),
                           KEY `fk_receipt_item_item_id` (`item_id`),
                           CONSTRAINT `fk_receipt_item_item_id` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



CREATE TABLE `megaphone` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `content` varchar(30) DEFAULT NULL,
                             `used_at` date NOT NULL,
                             `user_id` bigint NOT NULL,
                             `receipt_id` bigint DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             KEY `fk_megaphone_user_user_id` (`user_id`),
                             KEY `fk_megaphone_receipt_receipt_id` (`receipt_id`),
                             CONSTRAINT `fk_megaphone_receipt_receipt_id` FOREIGN KEY (`receipt_id`) REFERENCES `receipt` (`id`),
                             CONSTRAINT `fk_megaphone_user_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `noti` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `created_at` datetime NOT NULL,
                        `modified_at` datetime DEFAULT NULL,
                        `is_checked` bit(1) NOT NULL,
                        `message` varchar(255) DEFAULT NULL,
                        `user_id` bigint DEFAULT NULL,
                        `noti_type` varchar(15) NOT NULL,
                        PRIMARY KEY (`id`),
                        KEY `fk_noti_user_user_id` (`user_id`),
                        CONSTRAINT `fk_noti_user_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `pchange` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `created_at` datetime NOT NULL,
                           `modified_at` datetime DEFAULT NULL,
                           `ppp_result` int NOT NULL,
                           `game_id` bigint NOT NULL,
                           `exp` int NOT NULL,
                           `user_id` bigint NOT NULL,
                           `is_checked` bit(1) NOT NULL DEFAULT b'0',
                           PRIMARY KEY (`id`),
                           KEY `fk_pchange_user_user_id` (`user_id`),
                           KEY `fk_pchange_game_game_id` (`game_id`),
                           CONSTRAINT `fk_pchange_game_game_id` FOREIGN KEY (`game_id`) REFERENCES `game` (`id`) ON UPDATE CASCADE,
                           CONSTRAINT `fk_pchange_user_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `penalty` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `user_id` bigint NOT NULL,
                           `penalty_type` varchar(20) NOT NULL,
                           `message` varchar(100) DEFAULT NULL,
                           `start_time` datetime NOT NULL,
                           `penalty_time` int NOT NULL,
                           `created_at` datetime NOT NULL,
                           `modified_at` datetime NOT NULL,
                           PRIMARY KEY (`id`),
                           KEY `fk_penalty_user_user_id_idx` (`user_id`),
                           CONSTRAINT `fk_penalty_user_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `ranks` (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `created_at` datetime NOT NULL,
                         `modified_at` datetime DEFAULT NULL,
                         `losses` int NOT NULL,
                         `ppp` int NOT NULL,
                         `season_id` bigint NOT NULL,
                         `wins` int NOT NULL,
                         `status_message` varchar(255) DEFAULT NULL,
                         `user_id` bigint NOT NULL,
                         `tier_id` bigint DEFAULT NULL,
                         PRIMARY KEY (`id`),
                         KEY `fk_ranks_user_user_id` (`user_id`),
                         KEY `fk_ranks_season_season_id` (`season_id`),
                         KEY `fk_ranks_tier_tier_id` (`tier_id`),
                         CONSTRAINT `fk_ranks_season_season_id` FOREIGN KEY (`season_id`) REFERENCES `season` (`id`) ON UPDATE CASCADE,
                         CONSTRAINT `fk_ranks_tier_tier_id` FOREIGN KEY (`tier_id`) REFERENCES `tier` (`id`),
                         CONSTRAINT `fk_ranks_user_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `slot_management` (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `created_at` datetime NOT NULL,
                                   `modified_at` datetime DEFAULT NULL,
                                   `future_slot_time` int NOT NULL,
                                   `game_interval` int NOT NULL,
                                   `open_minute` int NOT NULL,
                                   `past_slot_time` int NOT NULL,
                                   `start_time` datetime NOT NULL,
                                   `end_time` datetime DEFAULT NULL,
                                   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `team` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `score` int DEFAULT NULL,
                        `win` bit(1) DEFAULT NULL,
                        `game_id` bigint NOT NULL,
                        PRIMARY KEY (`id`),
                        KEY `fk_team_game_game_id` (`game_id`),
                        CONSTRAINT `fk_team_game_game_id` FOREIGN KEY (`game_id`) REFERENCES `game` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `team_user` (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `team_id` bigint NOT NULL,
                             `user_id` bigint NOT NULL,
                             PRIMARY KEY (`id`),
                             KEY `fk_team_user_team_team_id` (`team_id`),
                             KEY `fk_team_user_user_user_id` (`user_id`),
                             CONSTRAINT `fk_team_user_team_team_id` FOREIGN KEY (`team_id`) REFERENCES `team` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
                             CONSTRAINT `fk_team_user_user_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `user_image` (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `user_id` bigint DEFAULT NULL,
                              `image_uri` varchar(255) DEFAULT NULL,
                              `created_at` datetime(6) NOT NULL,
                              `deleted_at` datetime(6) DEFAULT NULL,
                              `is_current` bit(1) NOT NULL DEFAULT b'1',
                              PRIMARY KEY (`id`),
                              KEY `fk_user_image_user_user_id` (`user_id`),
                              CONSTRAINT `fk_user_image_user_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


create or replace view v_teamuser as
select team.id teamId, team.score, team.win, g.id gameId, g.season_id seasonId, g.start_time startTime, g.status, g.mode, tu.user_id userId, u.intra_id intraId, u.image_uri image, u.total_exp
from team, team_user tu, user u, game g
where team.id=tu.team_id and u.id=tu.user_id and g.id=team.game_id;


create or replace view v_rank_game_detail as
select team.id teamId, team.score, team.win, g.id gameId, g.season_id seasonId, g.start_time startTime, g.end_time endTime, g.status, g.mode,
       tu.user_id userId, u.intra_id intraId, u.image_uri image, u.total_exp,
       r.wins, r.losses
from team, team_user tu, user u, game g, ranks r
where team.id=tu.team_id and u.id=tu.user_id and g.id=team.game_id and r.user_id = u.id and r.season_id = g.season_id;

