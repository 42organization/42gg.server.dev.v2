package com.gg.server.domain.match.dto;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.match.data.RedisMatchTime;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MatchStatusDto {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean isMatched;
    private List<String> myTeam;
    private List<String> enemyTeam;

    public MatchStatusDto(Game game, String myIntraId, String enemyIntraId) {
        this.startTime = game.getStartTime();
        this.endTime = game.getEndTime();
        this.isMatched = true;
        this.myTeam = List.of(myIntraId, enemyIntraId);
        this.enemyTeam = List.of();

    }

    public MatchStatusDto(RedisMatchTime redisMatchTime, Integer interval) {
        this.startTime = redisMatchTime.getStartTime();
        this.endTime = redisMatchTime.getStartTime().plusMinutes(interval);
        this.isMatched = false;
        this.myTeam = List.of();
        this.enemyTeam = List.of();
    }

    @Override
    public String toString() {
        return "CurrentMatchResponseDto{" +
                "startTime=" + startTime +
                "endTIme=" + endTime +
                ", myTeam=" + myTeam +
                ", enemyTeam=" + enemyTeam +
                ", isMatched=" + isMatched +
                '}';
    }
}
