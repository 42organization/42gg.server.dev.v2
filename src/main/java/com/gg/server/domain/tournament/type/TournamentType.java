package com.gg.server.domain.tournament.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Locale;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TournamentType {
    ROOKIE("rookie", "초보"),
    MASTER("master", "고수");

    private final String code;
    private final String desc;

    @JsonCreator
    public static TournamentType getEnumFromValue(String value) {
        if (value == null) return null;
        for(TournamentType e : values()) {
            if (e.name().equals(value)) {
                return e;
            } else if (e.code.toUpperCase(Locale.ROOT).equals(value.toUpperCase(Locale.ROOT))) {
                return e;
            }
        }
        return null;
    }
}
