package com.gg.server.global.exception.custom;

import com.gg.server.global.exception.ErrorCode;

public class BusinessException extends CustomRuntimeException {
    private String message;
    private ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage(), errorCode);
        this.errorCode = errorCode;
        this.message = errorCode.getMessage();
    }
}
