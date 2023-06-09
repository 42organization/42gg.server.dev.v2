package com.gg.server.domain.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Getter
@NoArgsConstructor
public class GameListResDto {
    private List<GameResultResDto> games;
    Boolean isLast;

    public GameListResDto(List<GameResultResDto> games, Boolean isLast) {
        this.games = games;
        this.isLast = isLast;
    }

    @Override
    public String toString() {
        return "GameListResDto{" +
                "games=" + games +
                ", isLast=" + isLast +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (!(obj instanceof GameListResDto)) {
            return false;
        } else {
            GameListResDto other = (GameListResDto) obj;
            return this.isLast == other.getIsLast()
                    && this.games.equals(other.getGames());
        }
    }
}
