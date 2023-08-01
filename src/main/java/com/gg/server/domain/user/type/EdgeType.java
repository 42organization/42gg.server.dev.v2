package com.gg.server.domain.user.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EdgeType {
    COLOR1(0, "color1"),
    COLOR2(1, "color2"),
    COLOR3(2, "color3");

    private final Integer value;
    private final String code;

    public static EdgeType of(String code) {
        return EdgeType.valueOf(code);
    }
}
