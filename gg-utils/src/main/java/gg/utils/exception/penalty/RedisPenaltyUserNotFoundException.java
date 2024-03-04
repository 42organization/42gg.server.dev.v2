package gg.utils.exception.penalty;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class RedisPenaltyUserNotFoundException extends NotExistException {
	public RedisPenaltyUserNotFoundException() {
		super("Redis에 Penalty User 데이터가 없습니다.", ErrorCode.REDIS_PENALTY_USER_NOT_FOUND);
	}
}
