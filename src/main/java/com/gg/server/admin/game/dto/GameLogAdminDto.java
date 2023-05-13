package com.gg.server.admin.game.dto;

import com.gg.server.domain.game.data.Game;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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

    public GameLogAdminDto(Game game, List<GameTeamAdminDto> gameTeamAdminDtoList) {
        this.gameId = game.getId();
        this.startAt = game.getStartTime();
        this.slotTime = game.getEndTime() == null ? null :
                String.valueOf(Duration.between(game.getStartTime().toLocalTime(), game.getEndTime().toLocalTime()).toMinutes());
        this.mode = game.getMode().getCode();
        this.team1 = gameTeamAdminDtoList.get(0);
        this.team2 = gameTeamAdminDtoList.get(1);
    }
}
