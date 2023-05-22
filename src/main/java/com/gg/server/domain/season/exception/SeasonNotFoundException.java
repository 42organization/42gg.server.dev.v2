package com.gg.server.domain.season.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class SeasonNotFoundException extends NotExistException {
    public SeasonNotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
