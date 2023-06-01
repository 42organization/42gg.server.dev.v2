create or replace view v_rank_game_detail as
select team.id teamId, team.score, team.win, g.id gameId, g.season_id seasonId, g.start_time startTime, g.status, g.mode,
       tu.user_id userId, u.intra_id intraId, u.image_uri image, u.total_exp,
       r.wins, r.losses
from team, team_user tu, user u, game g, ranks r
where team.id=tu.team_id and u.id=tu.user_id and g.id=team.game_id and r.user_id = u.id and r.season_id = g.season_id;