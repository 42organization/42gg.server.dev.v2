package com.gg.server.domain.game.dto;

import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.team.dto.TeamUserInfoDto;
import com.gg.server.domain.team.dto.TeamUserListDto;
import com.gg.server.global.utils.ExpLevelCalculator;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
public class GameResultResDto {
    private Long gameId;
    private String status;
    private Mode mode;
    private LocalDateTime time;
    private TeamUserListDto team1;
    private TeamUserListDto team2;

    public GameResultResDto(GameTeamUser game) {
        this.gameId = game.getGameId();
        this.status = game.getStatus().name();//name -> 대문자
        this.time = game.getStartTime();
        this.mode = game.getMode();
        team1 = new TeamUserListDto(Arrays.asList(
                new TeamUserInfoDto(game.getT1IntraId(), game.getT1Image(), game.getT1Exp())));
        team2 = new TeamUserListDto(Arrays.asList(
                new TeamUserInfoDto(game.getT1IntraId(), game.getT1Image(), game.getT1Exp())));
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
