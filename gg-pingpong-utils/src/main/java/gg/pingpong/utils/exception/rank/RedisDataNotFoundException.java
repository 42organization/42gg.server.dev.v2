package gg.pingpong.utils.exception.rank;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.NotExistException;

public class RedisDataNotFoundException extends NotExistException {
	public RedisDataNotFoundException() {
		super("Redis에 데이터가 없습니다.", ErrorCode.REDIS_RANK_NOT_FOUND);
	}
}
