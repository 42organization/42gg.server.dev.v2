package com.gg.server.domain.rank.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class RankNotFoundException extends NotExistException {
    public RankNotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
