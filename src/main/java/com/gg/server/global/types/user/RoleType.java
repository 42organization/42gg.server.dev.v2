package com.gg.server.global.types.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum RoleType {

    ADMIN("ROLE_ADMIN", "관리자", 2),
    USER("ROLE_USER", "일반 사용자", 1);

    private final String key;
    private final String displayName;
    private final Integer value;

    public static RoleType of(String key) {
        return Arrays.stream(RoleType.values())
                .filter(r -> r.getKey().equals(key))
                .findAny()
                .orElse(USER);
    }
}

