package com.gg.server.domain.user.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BackgroundType {
    COLOR1(0, "color1"),
    COLOR2(1, "color2"),
    COLOR3(2, "color3");

    private final Integer value;
    private final String code;

    public static BackgroundType of(String code) {
        return BackgroundType.valueOf(code);
    }
}