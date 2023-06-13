### delete key ###
alter table current_match drop foreign key FK4qtnfmeqkj0iy1qeghm6ywb6n;
alter table current_match drop foreign key FKl53dqtyiaemmm1tqdsdj55ng2;
alter table current_match drop foreign key FKmaa4irkiicemkq2gl3t0bqn6a;
alter table slot_team_user drop foreign key FKg9emy523qv3kx98dkb3xc8m9x;
alter table slot_team_user drop foreign key FKkr5xbxj8y5gcqivythvkwxmqw;
alter table slot_team_user drop foreign key FKpugruruole12eeo3hvqpt9s42;
alter table team drop foreign key FK23mky06ol3wc1dj8b11bknntp;
alter table noti drop foreign key FKehey4772h8bhsaix4x6tkrjjc;
alter table game drop foreign key FKjnhlg2dqkrjvaai3e70ymw339;
alter table game drop foreign key FKehwjicpbm71ks5yyuo7co77qj;
alter table game drop foreign key FKgb66of4ini2gvvu76o8pufcr5;
alter table pchange drop foreign key FKqu8jxac7ewvc26oc7hukcixxd;
alter table feedback drop foreign key FK7k33yw505d347mw3avr93akao;
alter table noti drop foreign key FKbjyk0xemxlmv8f6n7nxvx2akk;
alter table pchange drop foreign key FKjld3ge0jy4tjbqxso9wpck9r1;
alter table ranks drop foreign key FK9baj7lwqe9acxbeplowb01mn9;


drop table deleted_slot_team_user;
drop table current_match;
drop table event_user;
drop table ping_pong_event;
drop table user_refresh_token;
drop table visit;
####

### INT -> BIGINT ###
ALTER TABLE announcement MODIFY id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE feedback MODIFY id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE game MODIFY id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE noti MODIFY id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE pchange MODIFY id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE ranks MODIFY id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE season MODIFY id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE slot_management MODIFY id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE team MODIFY id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE slot_team_user CHANGE COLUMN team_user_id id BIGINT NOT NULL AUTO_INCREMENT;
ALTER TABLE user MODIFY id BIGINT NOT NULL AUTO_INCREMENT;

### team ###
SET SQL_SAFE_UPDATES=0;
alter table team add column game_id BIGINT;
UPDATE team t
  INNER JOIN game g ON g.slot_id = t.slot_id
  SET t.game_id = g.id;

DELETE FROM team
WHERE slot_id NOT IN (
  SELECT slot_id FROM game);

ALTER TABLE team
  ADD CONSTRAINT fk_team_game_game_id
    FOREIGN KEY (game_id) REFERENCES game(id)
      ON UPDATE CASCADE
      ON DELETE CASCADE;

alter table team modify score INT;
alter table team modify game_id BIGINT not null;
alter table team drop column slot_id;
alter table team drop head_count;
alter table team drop team_ppp;
SET SQL_SAFE_UPDATES=1;
####

### team_user ###
alter table slot_team_user modify team_id BIGINT not null;
alter table slot_team_user modify user_id BIGINT not null;
####

### announcement ###
SET SQL_SAFE_UPDATES=0;
alter table announcement modify content VARCHAR(1000) not null;
alter table announcement modify creator_intra_id VARCHAR(30) not null;
alter table announcement modify deleter_intra_id VARCHAR(30);
alter table announcement change deleted_time deleted_at DATETIME;
alter table announcement drop column is_del;
alter table announcement change column created_time created_at DATETIME not null;
alter table announcement add column modified_at DATETIME;
update announcement set modified_at=deleted_at where modified_at is null;
SET SQL_SAFE_UPDATES=1;
####

### feed_back ###
SET SQL_SAFE_UPDATES=0;
ALTER TABLE feedback CHANGE user_id intra_id VARCHAR(100);
alter table feedback add column user_id BIGINT;

UPDATE feedback f
  INNER JOIN user u
ON f.intra_id = u.intra_id
  SET f.user_id = u.id;

alter table feedback drop intra_id;

alter table feedback
  add constraint fk_feedback_user_user_id
    foreign key (user_id) REFERENCES user(id)
      ON UPDATE CASCADE;

ALTER TABLE feedback CHANGE category enum_category INT;
alter table feedback add column category VARCHAR(15) not null;
update feedback set category="BUG" where enum_category=0;
update feedback set category="GAMERESULT" where enum_category=1;
update feedback set category="COMPLAINT" where enum_category=2;
update feedback set category="CHEERS" where enum_category=3;
update feedback set category="OPINION" where enum_category=4;
update feedback set category="ETC" where enum_category=5;
alter table feedback drop enum_category;
SET SQL_SAFE_UPDATES=1;
####

### GAME ###
SET SQL_SAFE_UPDATES=0;
alter table game ADD COLUMN start_time DATETIME;
alter table game add column end_time DATETIME;

alter table game change season season_id bigint not null;

alter table game
  add constraint fk_game_season_season_id
    foreign key (season_id) REFERENCES season(id)
      ON UPDATE CASCADE;

ALTER TABLE game CHANGE status enum_status INT;
alter table game add column status VARCHAR(10) not null;
update game set status="LIVE" where enum_status=0;
update game set status="WAIT" where enum_status=1;
update game set status="END" where enum_status=2;
alter table game drop enum_status;

ALTER TABLE game CHANGE mode enum_mode INT;
alter table game add column mode VARCHAR(10) not null;
update game set mode="NORMAL" where enum_mode=1;
update game set mode="RANK" where enum_mode=2;
alter table game drop column enum_mode;

UPDATE game g
  INNER JOIN slot s ON g.slot_id = s.id
  SET g.start_time = s.time, g.end_time = s.end_time;

alter table game modify start_time DATETIME not null;

alter table game drop column team1_id;
alter table game drop column team2_id;
alter table game drop column slot_id;
alter table game drop column time;
alter table game drop type;
SET SQL_SAFE_UPDATES=1;
####

### NOTI ###
SET SQL_SAFE_UPDATES=0;
ALTER TABLE noti CHANGE user_id intra_id VARCHAR(100);
alter table noti add column user_id BIGINT;

UPDATE noti n
  INNER JOIN user u
ON n.intra_id = u.intra_id
  SET n.user_id = u.id;

alter table noti
  add constraint fk_noti_user_user_id
    foreign key (user_id) REFERENCES user(id)
      ON UPDATE CASCADE;

ALTER TABLE noti CHANGE noti_type enum_noti_type INT;
alter table noti add column noti_type VARCHAR(15) not null;
update noti set noti_type="MATCHED" where enum_noti_type=0;
update noti set noti_type="CANCELEDBYMAN" where enum_noti_type=1;
update noti set noti_type="CANCELEDBYTIME" where enum_noti_type=2;
update noti set noti_type="IMMINENT" where enum_noti_type=3;
update noti set noti_type="ANNOUNCE" where enum_noti_type=4;
alter table noti drop column enum_noti_type;

alter table noti drop intra_id;
alter table noti drop slot_id;
delete from noti;
ALTER TABLE noti AUTO_INCREMENT = 1;
SET SQL_SAFE_UPDATES=1;
####

### PCHANGE ###
SET SQL_SAFE_UPDATES=0;
ALTER TABLE pchange CHANGE user_id intra_id VARCHAR(100);
alter table pchange add column user_id BIGINT;

UPDATE pchange p
  INNER JOIN user u
ON p.intra_id = u.intra_id
  SET p.user_id = u.id;

alter table pchange modify game_id bigint not null;
alter table pchange
  add constraint fk_pchange_user_user_id
    foreign key (user_id) REFERENCES user(id)
      ON UPDATE CASCADE;

alter table pchange
  add constraint fk_pchange_game_game_id
    foreign key (game_id) REFERENCES game(id)
      ON UPDATE CASCADE;

alter table pchange modify user_id BIGINT not null;
alter table pchange modify game_id BIGINT not null;

alter table pchange drop intra_id;
alter table pchange drop ppp_change;
alter table pchange drop exp_change;
alter table pchange change exp_result exp INT not null;
SET SQL_SAFE_UPDATES=1;
####

### RANKS ###
SET SQL_SAFE_UPDATES=0;
ALTER TABLE ranks CHANGE user_id intra_id VARCHAR(100);
alter table ranks add column user_id BIGINT;

UPDATE ranks r
  INNER JOIN user u
ON r.intra_id = u.intra_id
  SET r.user_id = u.id;

alter table ranks modify user_id bigint not null;
alter table ranks
  add constraint fk_ranks_user_user_id
    foreign key (user_id) REFERENCES user(id)
      ON UPDATE CASCADE;

alter table ranks modify season_id bigint not null;
alter table ranks
  add constraint fk_ranks_season_season_id
    foreign key (season_id) REFERENCES season(id)
      ON UPDATE CASCADE;

alter table ranks drop intra_id;
alter table ranks drop game_type;
alter table ranks drop racket_type;
####

### season ###
alter table season modify season_name VARCHAR(20) not null;
alter table season drop column season_mode;
####

### team_user ###
alter table slot_team_user rename to team_user;

alter table team_user drop created_at;
alter table team_user drop modified_at;
alter table team_user drop slot_id;
####

### user ###
ALTER TABLE user CHANGE racket_type enum_racket_type INT;
alter table user add column racket_type VARCHAR(10);
update user set racket_type="PENHOLDER" where enum_racket_type=0;
update user set racket_type="SHAKEHAND" where enum_racket_type=1;
update user set racket_type="DUAL" where enum_racket_type=2;
update user set racket_type="NONE" where enum_racket_type=3;
alter table user drop column enum_racket_type;

ALTER TABLE user CHANGE role_type enum_role_type INT;
alter table user add column role_type VARCHAR(10);
update user set role_type="ADMIN" where enum_role_type=0;
update user set role_type="USER" where enum_role_type=1;
update user set role_type="USER" where enum_role_type=2;
alter table user drop column enum_role_type;

alter table user add column kakao_id BIGINT;

alter table user drop column status_message;
alter table user modify intra_id VARCHAR(30) not null;
alter table user modify e_mail VARCHAR(60);
alter table user modify racket_type VARCHAR(10);
alter table user modify role_type VARCHAR(10) not null;
alter table user modify sns_noti_opt VARCHAR(10);
alter table user drop ppp;

alter table ranks drop ranking;
####

### add Penalty table ###
DROP TABLE IF EXISTS `penalty`;

CREATE TABLE `penalty` (
                         `id` BIGINT NOT NULL AUTO_INCREMENT,
                         `user_id` BIGINT NOT NULL,
                         `penalty_type` VARCHAR(20) NOT NULL,
                         `message` VARCHAR(100) NULL,
                         `start_time` DATETIME NOT NULL,
                         `penalty_time` INT NOT NULL,
                         `created_at` DATETIME NOT NULL,
                         `modified_at` DATETIME NOT NULL,
                         PRIMARY KEY (`id`),
                         INDEX `fk_penalty_user_user_id_idx` (`user_id` ASC) VISIBLE,
                         CONSTRAINT `fk_penalty_user_user_id`
                           FOREIGN KEY (`user_id`)
                             REFERENCES user (`id`)
                             ON DELETE NO ACTION
                             ON UPDATE NO ACTION);
####

### slot_management ###
alter table slot_management add column start_time DATETIME;
alter table slot_management add column end_time DATETIME;

SET SQL_SAFE_UPDATES=0;
DELETE t1
FROM slot_management t1
JOIN (SELECT MAX(created_at) AS max_created_at FROM slot_management) t2
WHERE t1.created_at < t2.max_created_at;
SET SQL_SAFE_UPDATES=0;

UPDATE slot_management
SET start_time = created_at;

alter table slot_management modify start_time DATETIME NOT NULL;
####

### drop tables ###
drop table slot;
####

### views ###
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
####