alter table user modify intra_id VARCHAR(30) not null;
alter table user modify e_mail VARCHAR(60);
alter table user modify racket_type VARCHAR(10);
alter table user modify role_type VARCHAR(10) not null;
alter table user modify sns_noti_opt VARCHAR(10);
alter table user drop ppp;

alter table ranks drop ranking;

create or replace view v_teamuser as
    select team.id teamId, g.id gameId, g.start_time startTime, g.status, g.mode, tu.user_id userId, u.intra_id intraId, u.image_uri image, u.total_exp
    from team, team_user tu, user u, game g
    where team.id=tu.team_id and u.id=tu.user_id and g.id=team.game_id;