package com.gg.server.domain.match.type;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class MatchKey {
    public static final String USER = "MATCH:USER:";
    public static final String TIME = "MATCH:TIME:";
    private static final String conjunctive = ":";

    public static String getUserTime(Long userId, LocalDateTime startTime) {
        return USER + userId + conjunctive + startTime;
    }

    public static String getUser(Long userId) {
        return USER + userId;
    }

    public static String getTime(LocalDateTime startTime) {
        return TIME + startTime;
    }

    public static String getAllTime() {
        return TIME;
    }
}
