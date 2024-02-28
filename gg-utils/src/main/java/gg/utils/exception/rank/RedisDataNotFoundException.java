package gg.utils.exception.rank;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class RedisDataNotFoundException extends NotExistException {
	public RedisDataNotFoundException() {
		super("Redis에 데이터가 없습니다.", ErrorCode.REDIS_RANK_NOT_FOUND);
	}
}
