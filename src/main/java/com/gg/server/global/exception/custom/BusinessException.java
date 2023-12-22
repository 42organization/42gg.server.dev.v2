package com.gg.server.global.exception.custom;

import com.gg.server.global.exception.ErrorCode;

public class BusinessException extends CustomRuntimeException{
    public BusinessException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
    }
}
