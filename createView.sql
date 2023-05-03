create or replace view v_teamuser as
    select team.id teamId, g.id gameId, g.start_time startTime, tu.user_id userId, u.intra_id intraId, u.image_uri image, u.total_exp
    from team, team_user tu, user u, game g
    where team.id=tu.team_id and u.id=tu.user_id and g.id=team.game_id;