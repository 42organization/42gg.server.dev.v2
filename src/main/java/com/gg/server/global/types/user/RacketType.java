package com.gg.server.global.types.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gg.server.global.types.Constant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@Getter
@RequiredArgsConstructor
public enum RacketType implements Constant {
    PENHOLDER("penholder"),
    SHAKEHAND("shakehand"),
    DUAL("dual"),
    NONE("none");

    private final String code;

    @JsonCreator
    public static RacketType getEnumFromValue(String value) {
        for(RacketType e : values()) {
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
