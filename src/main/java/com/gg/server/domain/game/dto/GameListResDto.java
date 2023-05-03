package com.gg.server.domain.game.dto;

import lombok.Getter;
import java.util.*;

@Getter
public class GameListResDto {
    private List<NormalGameResDto> games;
    boolean isLast;

    public GameListResDto(List<NormalGameResDto> games, boolean isLast) {
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
}
