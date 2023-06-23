package com.gg.server.domain.user.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum RoleType {

    ADMIN("ADMIN", "관리자"),
    USER("USER", "일반 사용자"),
    GUEST("GUEST", "게스트");

    private final String key;
    private final String displayName;

    public static RoleType of(String key) {
        return Arrays.stream(RoleType.values())
                .filter(r -> r.getKey().equals(key))
                .findAny()
                .orElse(USER);
    }
}

