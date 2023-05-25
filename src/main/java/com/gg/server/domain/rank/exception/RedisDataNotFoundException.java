package com.gg.server.domain.rank.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class RedisDataNotFoundException extends NotExistException {
    public RedisDataNotFoundException() {
        super("Redis에 데이터가 없습니다.", ErrorCode.REDIS_RANK_NOT_FOUND);
    }
}
