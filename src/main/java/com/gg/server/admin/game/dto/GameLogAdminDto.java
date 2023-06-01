package com.gg.server.admin.game.dto;

import com.gg.server.domain.game.dto.GameTeamUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameLogAdminDto {
    private Long gameId;
    private LocalDateTime startAt;
    private String slotTime;
    private String mode;
    private GameTeamAdminDto team1;
    private GameTeamAdminDto team2;

    public GameLogAdminDto(GameTeamUser game) {
        this.gameId = game.getGameId();
        this.startAt = game.getStartTime();
        this.slotTime = game.getEndTime() == null ? null :
                String.valueOf(Duration.between(game.getStartTime().toLocalTime(), game.getEndTime().toLocalTime()).toMinutes());
        this.mode = game.getMode().getCode();
        this.team1 = new GameTeamAdminDto(game.getT1IntraId(), game.getT1TeamId(), game.getT1Score(), game.getT1IsWin());
        this.team2 = new GameTeamAdminDto(game.getT2IntraId(), game.getT2TeamId(), game.getT2Score(), game.getT2IsWin());
    }
}
