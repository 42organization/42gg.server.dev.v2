package com.gg.server.domain.penalty.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class RedisPenaltyUserNotFoundException extends NotExistException {
    public RedisPenaltyUserNotFoundException() {
        super("Redis에 Penalty User 데이터가 없습니다.", ErrorCode.REDIS_PENALTY_USER_NOT_FOUND);
    }
}
