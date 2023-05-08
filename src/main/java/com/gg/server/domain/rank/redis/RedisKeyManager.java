package com.gg.server.domain.rank.redis;

public class RedisKeyManager {
    private static final String ZSetKeyPrefix = "rank:ZSet:";
    private static final String HashKeyPrefix = "rank:hash:";

    public static String getZSetKey(Long seasonId) {
        return ZSetKeyPrefix + seasonId;
    }

    public static String getHashKey(Long seasonId) {
        return HashKeyPrefix + seasonId;
    }

}
