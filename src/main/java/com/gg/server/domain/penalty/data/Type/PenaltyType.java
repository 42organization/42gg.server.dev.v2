package com.gg.server.domain.penalty.data.Type;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Locale;

@Getter
@RequiredArgsConstructor
public enum PenaltyType {
    NONE("NONE"),
    NOSHOW("NOSHOW"),
    CANCEL("CANCEL");

    private final String code;

    public static PenaltyType of(String code) {
        return Arrays.stream(PenaltyType.values())
                .filter(penaltyType-> penaltyType.getCode().equals(code))
                .findAny()
                .orElse(NONE);
    }

    @JsonCreator
    public static PenaltyType getEnumFromValue(String value) {
        for(PenaltyType e : values()) {
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
