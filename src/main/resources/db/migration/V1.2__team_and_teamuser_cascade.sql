SET SQL_SAFE_UPDATES=0;

DELETE FROM team_user
WHERE team_id NOT IN (SELECT id FROM team);
delete FROM game
WHERE id NOT IN (SELECT game_id FROM team);

SET SQL_SAFE_UPDATES=1;

alter table team_user
add constraint fk_team_user_team_team_id
foreign key (team_id) REFERENCES team(id)
ON UPDATE CASCADE
ON DELETE CASCADE;

alter table team_user
add constraint fk_team_user_user_user_id
foreign key (user_id) REFERENCES user(id)
ON UPDATE CASCADE;