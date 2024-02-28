package gg.utils;

public class RedisKeyManager {
	private static final String ZSetKeyPrefix = "rank:ZSet:";
	private static final String HashKeyPrefix = "rank:hash:";
	private static final String RefKeyPrefix = "refresh:token:";

	public static String getZSetKey(Long seasonId) {
		return ZSetKeyPrefix + seasonId;
	}

	public static String getHashKey(Long seasonId) {
		return HashKeyPrefix + seasonId;
	}

	public static String getRefKey(Long id) {
		return RefKeyPrefix + id.toString();
	}
}
