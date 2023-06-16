package com.gg.server.domain.game.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gg.server.domain.match.data.RedisMatchUser;
import com.gg.server.domain.match.type.Option;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@Getter
@RequiredArgsConstructor
public enum Mode {
    NORMAL("normal"), RANK("rank");
    private final String code;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Mode getEnumValue(String code) {
        for(Mode e : values()) {
            if(e.code.equals(code)) {
                return e;
            }
            else if (e.code.toUpperCase(Locale.ROOT).equals(code.toUpperCase(Locale.ROOT)))
                return e;
        }
        return null;
    }

    public static Mode ofPlayers(List<RedisMatchUser> players, Integer pppGap) {
        if (players.stream().allMatch(player -> player.getOption().equals(Option.BOTH))) {
            if (Math.abs(players.get(0).getPpp() - players.get(1).getPpp()) <= pppGap) {
                return Mode.RANK;
            }
            return Mode.NORMAL;
        }
        if (!players.get(0).getOption().equals(Option.BOTH)) {
            return Mode.getEnumValue(players.get(0).getOption().getCode());
        }
        return Mode.getEnumValue(players.get(1).getOption().getCode());
    }
}
