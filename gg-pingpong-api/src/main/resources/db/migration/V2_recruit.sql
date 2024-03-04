CREATE TABLE `application` (
       `id` bigint NOT NULL AUTO_INCREMENT,
       `created_at` datetime(6) NOT NULL,
       `modified_at` datetime(6) DEFAULT NULL,
       `is_deleted` bit(1) DEFAULT false,
       `status` varchar(15) NOT NULL ,
       `recruit_id` bigint NOT NULL ,
       `user_id` bigint NOT NULL ,
       PRIMARY KEY (`id`),
       KEY `FKnmih1vdymw1494hdj04in2e5h` (`recruit_id`),
       KEY `FKldca8xj6lqb3rsqawrowmkqbg` (`user_id`),
       CONSTRAINT `FKldca8xj6lqb3rsqawrowmkqbg` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
       CONSTRAINT `FKnmih1vdymw1494hdj04in2e5h` FOREIGN KEY (`recruit_id`) REFERENCES `recruitments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci


CREATE TABLE `application_answer` (
      `id` bigint NOT NULL AUTO_INCREMENT,
      `created_at` datetime(6) NOT NULL,
      `modified_at` datetime(6) DEFAULT NULL,
      `application_id` bigint NOT NULL ,
      `question_id` bigint NOT NULL ,
      PRIMARY KEY (`id`),
      KEY `FKn2ayp7tptdv0yycdqkp2hcm63` (`application_id`),
      KEY `FK59sj9jdfki14kkp34kc2jdhyj` (`question_id`),
      CONSTRAINT `FK59sj9jdfki14kkp34kc2jdhyj` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`),
      CONSTRAINT `FKn2ayp7tptdv0yycdqkp2hcm63` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci


CREATE TABLE `application_answer_check_list` (
     `id` bigint NOT NULL AUTO_INCREMENT,
     `created_at` datetime(6) NOT NULL,
     `modified_at` datetime(6) DEFAULT NULL,
     `application_answer_id` bigint NOT NULL,
     `check_list_id` bigint NOT NULL,
     PRIMARY KEY (`id`),
     KEY `FKqdnt92yg8t27q74he0ersyiax` (`application_answer_id`),
     KEY `FK3mf6hfr08f2ex01aqejikxk9w` (`check_list_id`),
     CONSTRAINT `FK3mf6hfr08f2ex01aqejikxk9w` FOREIGN KEY (`check_list_id`) REFERENCES `check_list` (`id`),
     CONSTRAINT `FKqdnt92yg8t27q74he0ersyiax` FOREIGN KEY (`application_answer_id`) REFERENCES `application_answer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `application_answer_text` (
   `id` bigint NOT NULL AUTO_INCREMENT,
   `created_at` datetime(6) NOT NULL,
   `modified_at` datetime(6) DEFAULT NULL,
   `answer` varchar(1000) DEFAULT NULL,
   `application_answer_id` bigint NOT NULL ,
   PRIMARY KEY (`id`),
   KEY `FKlhk4m3hi4r3v8xqk8lx4bx5g7` (`application_answer_id`),
   CONSTRAINT `FKlhk4m3hi4r3v8xqk8lx4bx5g7` FOREIGN KEY (`application_answer_id`) REFERENCES `application_answer` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci


CREATE TABLE `recruit_status` (
      `id` bigint NOT NULL AUTO_INCREMENT,
      `created_at` datetime(6) NOT NULL,
      `modified_at` datetime(6) DEFAULT NULL,
      `interview_date` datetime(6) DEFAULT NULL,
      `application_id` bigint NOT NULL ,
      PRIMARY KEY (`id`),
      KEY `FKrn9y1gwvfmkkoshsxyx3l4pbn` (`application_id`),
      CONSTRAINT `FKrn9y1gwvfmkkoshsxyx3l4pbn` FOREIGN KEY (`application_id`) REFERENCES `application` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `recruitments` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) NOT NULL,
    `modified_at` datetime(6) DEFAULT NULL,
    `contents` varchar(3000) DEFAULT NULL,
    `end_time` datetime(6) NOT NULL,
    `generation` varchar(50) DEFAULT NULL,
    `is_deleted` bit(1) DEFAULT false,
    `is_finish` bit(1) DEFAULT false,
    `start_time` datetime(6) NOT NULL,
    `title` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `question` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `created_at` datetime(6) NOT NULL,
    `modified_at` datetime(6) DEFAULT NULL,
    `input_type` varchar(20) NOT NULL,
    `question` varchar(300) DEFAULT NULL,
    `sort_num` int NOT NULL,
    `recruit_id` bigint NOT NULL ,
    PRIMARY KEY (`id`),
    KEY `FK44dkmx1sa8ssxjd7u5ne0ti30` (`recruit_id`),
    CONSTRAINT `FK44dkmx1sa8ssxjd7u5ne0ti30` FOREIGN KEY (`recruit_id`) REFERENCES `recruitments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `check_list` (
      `id` bigint NOT NULL AUTO_INCREMENT,
      `created_at` datetime(6) NOT NULL,
      `modified_at` datetime(6) DEFAULT NULL,
      `content` varchar(100) DEFAULT NULL,
      `question_id` bigint NOT NULL,
      PRIMARY KEY (`id`),
      KEY `FKksl9r4adqk1aih1fn5kihxh7b` (`question_id`),
      CONSTRAINT `FKksl9r4adqk1aih1fn5kihxh7b` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

CREATE TABLE `result_message` (
      `id` bigint NOT NULL AUTO_INCREMENT,
      `created_at` datetime(6) NOT NULL,
      `modified_at` datetime(6) DEFAULT NULL,
      `content` varchar(100) DEFAULT NULL,
      `is_use` bit(1) DEFAULT false,
      `message_type` varchar(15) NOT NULL,
      PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
