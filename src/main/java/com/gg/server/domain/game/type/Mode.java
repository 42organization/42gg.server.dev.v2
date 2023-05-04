package com.gg.server.domain.game.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@Getter
@RequiredArgsConstructor
public enum Mode {
    NORMAL("NORMAL"), RANK("RANK");
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

}
