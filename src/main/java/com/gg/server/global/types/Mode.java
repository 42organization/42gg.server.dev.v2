package com.gg.server.global.types;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@Getter
@RequiredArgsConstructor
public enum Mode {
    BOTH(0,"both"), NORMAL(1,"normal"), RANK(2,"rank");
    // 모드는 3가지가 있음.
    // type 형태이기 때문에 global 안에 type 도메인 안에 넣었음

    private final Integer value;
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
