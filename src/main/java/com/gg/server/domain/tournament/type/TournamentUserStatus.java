package com.gg.server.domain.tournament.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Locale;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TournamentUserStatus {
    PLAYER("player", "토너먼트 참가자"),
    WAIT("wait", "토너먼트 대기자"),
    BEFORE("before", "토너먼트 신청 전");

    private final String code;
    private final String desc;

    @JsonCreator
    public static TournamentUserStatus getEnumFromValue(String value) {
        if (value == null) return null;
        for(TournamentUserStatus e : values()) {
            if (e.name().equals(value)) {
                return e;
            } else if (e.code.toUpperCase(Locale.ROOT).equals(value.toUpperCase(Locale.ROOT))) {
                return e;
            }
        }
        return null;
    }
}
