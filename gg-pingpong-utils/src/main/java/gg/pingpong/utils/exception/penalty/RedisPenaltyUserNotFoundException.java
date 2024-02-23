package gg.pingpong.utils.exception.penalty;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.NotExistException;

public class RedisPenaltyUserNotFoundException extends NotExistException {
	public RedisPenaltyUserNotFoundException() {
		super("Redis에 Penalty User 데이터가 없습니다.", ErrorCode.REDIS_PENALTY_USER_NOT_FOUND);
	}
}
