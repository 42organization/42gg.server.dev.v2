create or replace view v_teamuser as
    select team.id teamId, team.score, team.win, g.id gameId, g.season_id seasonId, g.start_time startTime, g.status, g.mode, tu.user_id userId, u.intra_id intraId, u.image_uri image, u.total_exp
    from team, team_user tu, user u, game g
    where team.id=tu.team_id and u.id=tu.user_id and g.id=team.game_id;

create or replace VIEW `v_rank_game_detail` AS
select `team`.`id` AS `teamId`,`team`.`score` AS `score`,`team`.`win` AS `win`,`g`.`id` AS `gameId`,`g`.`season_id` AS `seasonId`,`g`.`start_time` AS `startTime`,`g`.`end_time` AS `endTime`,`g`.`status` AS `status`,`g`.`mode` AS `mode`,`tu`.`user_id` AS `userId`,`u`.`intra_id` AS `intraId`,`u`.`image_uri` AS `image`,`u`.`total_exp` AS `total_exp`,`r`.`wins` AS `wins`,`r`.`losses` AS `losses`
from ((((`team` join `team_user` `tu`) join `user` `u`) join `game` `g`) join `ranks` `r`)
where ((`team`.`id` = `tu`.`team_id`) and (`u`.`id` = `tu`.`user_id`) and (`g`.`id` = `team`.`game_id`) and (`r`.`user_id` = `u`.`id`) and (`r`.`season_id` = `g`.`season_id`));