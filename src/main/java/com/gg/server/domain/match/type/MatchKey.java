package com.gg.server.domain.match.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Locale;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MatchKey {
    USER("MATCH:USER:"),
    TIME("MATCH:TIME:");
    private final String code;
    @JsonCreator
    public static MatchKey getEnumFromValue(String value) {
        for(MatchKey e : values()) {
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
