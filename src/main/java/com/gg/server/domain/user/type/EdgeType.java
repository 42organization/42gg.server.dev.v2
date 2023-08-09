package com.gg.server.domain.user.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EdgeType {
    WRONG("wrong"),
    BASIC("basic"),
    COLOR2("color2"),
    COLOR3("color3");

    private final String url;

    @JsonCreator
    public static EdgeType getEnumFromValue(String edgeDto) {
        for (EdgeType e : EdgeType.values()) {
            if (e.toString().equals(edgeDto)) {
                return e;
            }
        }
        return WRONG;
    }
}