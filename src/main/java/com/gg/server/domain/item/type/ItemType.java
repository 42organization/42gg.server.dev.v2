package com.gg.server.domain.item.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ItemType {
    MEGAPHONE(0, "megaphone"),
    PROFILE_BACKGROUND(1, "profile_background"),
    PROFILE_BAND(2, "profile_band"),
    TEXT_COLOR(3, "text_color"),
    PROFILE_IMAGE(4, "profile_image");

    private final Integer value;
    private final String code;

    public static ItemType of(String code) {
        return ItemType.valueOf(code.toUpperCase());
    }

    public Integer getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }
}
