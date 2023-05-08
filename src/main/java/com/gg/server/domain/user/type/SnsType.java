package com.gg.server.domain.user.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SnsType {

    NONE(0, "NONE"),
    SLACK(1, "SLACK"),
    EMAIL(2, "EMAIL"),
    BOTH(3, "BOTH");

    private final Integer value;
    private final String code;

    public static SnsType of(String code) {
        return Arrays.stream(SnsType.values())
                .filter(snsType-> snsType.getCode().equals(code))
                .findAny()
                .orElse(SLACK);
    }

}
