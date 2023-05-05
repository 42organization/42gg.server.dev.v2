package com.gg.server.domain.rank.redis;

public class RedisKeyManager {
    private static final String ZSetKeyPrefix = "rank:ZSet:";
    private static final String HashKeyPrefix = "rank:hash:";

    static String getZSetKey(Long seasonId) {
        return ZSetKeyPrefix + seasonId;
    }

    static String getHashKey(Long seasonId) {
        return HashKeyPrefix + seasonId;
    }

}
