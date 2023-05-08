package com.gg.server.global.exception.custom;

import com.gg.server.global.exception.ErrorCode;

public class NotExistException extends CustomRuntimeException {
    private ErrorCode errorCode;

    public NotExistException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
