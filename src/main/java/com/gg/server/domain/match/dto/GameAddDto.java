package com.gg.server.domain.match.dto;

import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.match.data.RedisMatchUser;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.user.dto.UserDto;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor (access = AccessLevel.PROTECTED)
public class GameAddDto {
    private LocalDateTime startTime;
    private Season season;
    private Long playerId;
    private Long enemyId;
    private Mode mode;

    public GameAddDto(LocalDateTime startTime, Season season, RedisMatchUser player,
                      RedisMatchUser enemy) {
        this.startTime = startTime;
        this.season = season;
        this.playerId = player.getUserId();
        this.enemyId = enemy.getUserId();
        this.mode = Mode.ofPlayers(List.of(player, enemy), season.getPppGap());
    }

}
