package com.gg.server.domain.game.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Getter
@NoArgsConstructor
public class GameListResDto {
    private List<NormalGameResDto> games;
    Boolean isLast;

    public GameListResDto(List<NormalGameResDto> games, Boolean isLast) {
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
