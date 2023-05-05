package com.gg.server.domain.match.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Locale;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MatchStatus {
    OPEN("open"),
    CLOSE("close"),
    MYTABLE("mytable");
    private final String code;
    @JsonCreator
    public static MatchStatus getEnumFromValue(String value) {
        for (MatchStatus e : values()) {
            if(e.code.equals(value)) {
                return e;
            }
            else if (e.code.toUpperCase(Locale.ROOT).equals(value.toUpperCase(Locale.ROOT))) {
                return e;
            }
        }
        return null;
    }
}
