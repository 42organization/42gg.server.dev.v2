package com.gg.server.domain.game.dto;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.team.dto.TeamUserInfoDto;
import com.gg.server.domain.team.dto.TeamUserListDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class NormalGameResDto {
    private Long gameId;
    private String status;
    private LocalDateTime time;
    private TeamUserListDto team1;
    private TeamUserListDto team2;

    public NormalGameResDto(Game game) {
        this.gameId = game.getId();
        this.status = game.getStatus().getCode();//name -> 대문자
        this.time = game.getStartTime();
    }

    public void setTeamList(GameTeamUser teamUser) {
        List<TeamUserInfoDto> team = new ArrayList<>();
        team.add(TeamUserInfoDto.builder()
                .intraId(teamUser.getT1IntraId())
                .userImageUri(teamUser.getT1Image())
                .level(teamUser.getT1Exp())
                .build());
        team1 = new TeamUserListDto(team);
        team.clear();
        team.add(TeamUserInfoDto.builder()
                .intraId(teamUser.getT2IntraId())
                .userImageUri(teamUser.getT2Image())
                .level(teamUser.getT2Exp())
                .build());
        team2 = new TeamUserListDto(team);
    }

    @Override
    public String toString() {
        return "NormalGameResDto{" +
                "gameId=" + gameId +
                ", status='" + status + '\'' +
                ", time=" + time +
                ", team1=" + team1 +
                ", team2=" + team2 +
                '}';
    }
}
