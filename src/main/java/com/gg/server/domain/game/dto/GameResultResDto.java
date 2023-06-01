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
        if (mode == Mode.NORMAL) {
            team1 = new TeamUserListDto(Arrays.asList(
                    new TeamUserInfoDto(game.getT1IntraId(), game.getT1Image(), game.getT1Exp(), null, null)), null, null);
            team2 = new TeamUserListDto(Arrays.asList(
                    new TeamUserInfoDto(game.getT2IntraId(), game.getT2Image(), game.getT2Exp(), null, null)), null, null);
        } else {
            team1 = new TeamUserListDto(Arrays.asList(
                    new TeamUserInfoDto(game.getT1IntraId(), game.getT1Image(), game.getT1Exp(), game.getT1Wins(), game.getT1Losses())), game.getT1IsWin(), game.getT1Score());
            team2 = new TeamUserListDto(Arrays.asList(
                    new TeamUserInfoDto(game.getT2IntraId(), game.getT2Image(), game.getT2Exp(), game.getT2Wins(), game.getT2Losses())), game.getT2IsWin(), game.getT2Score());
        }
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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof GameResultResDto)) {
            return false;
        } else {
            GameResultResDto other = (GameResultResDto) obj;
            return this.gameId.equals(other.getGameId())
                    && this.status.equals(other.getStatus())
                    && this.mode.equals(other.getMode())
                    && this.time.equals(other.getTime())
                    && this.team1.equals(other.getTeam1())
                    && this.team2.equals(other.getTeam2());
        }
    }
}