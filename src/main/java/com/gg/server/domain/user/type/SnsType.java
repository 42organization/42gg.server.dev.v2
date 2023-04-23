package com.gg.server.domain.user.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Locale;

@Getter
@RequiredArgsConstructor
public enum SnsType {

    NONE(0, "NONE"),
    SLACK(1, "SLACK"),
    EMAIL(2, "EMAIL"),
    BOTH(3, "BOTH");

    private final Integer value;
    private final String code;

    public static SnsType of(Integer code) {
        return Arrays.stream(SnsType.values())
                .filter(snsType-> snsType.getCode().equals(code))
                .findAny()
                .orElse(SLACK);
    }

    @JsonCreator
    public static SnsType getEnumFromCode(String code) {
        for(SnsType e : values()) {
            if(e.code.equals(code)) {
                return e;
            }
            else if (e.code.toUpperCase(Locale.ROOT).equals(code.toUpperCase(Locale.ROOT))) {
                return e;
            }
        }
        return null;
    }

}
