package com.gg.server.domain.team.dto;

import com.gg.server.domain.game.dto.GameTeamUserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class MatchTeamsInfoDto {
    private TeamInfo myTeam;
    private TeamInfo enemyTeam;

    public MatchTeamsInfoDto(List<GameTeamUserInfo> infos, Long teamId) {
        if (teamId == null) return;
        myTeam = new TeamInfo();
        enemyTeam = new TeamInfo();
        for (GameTeamUserInfo info :
                infos) {
            if (teamId.equals(info.getTeamId())) {
                myTeam.setTeam(info.getTeamId(), info.getScore());
                myTeam.addPlayer(info);
            } else {
                enemyTeam.setTeam(info.getTeamId(), info.getScore());
                enemyTeam.addPlayer(info);
            }
        }
    }
}
