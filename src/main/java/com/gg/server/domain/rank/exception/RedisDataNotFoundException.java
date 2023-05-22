package com.gg.server.domain.rank.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class RedisDataNotFoundException extends NotExistException {
    public RedisDataNotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
