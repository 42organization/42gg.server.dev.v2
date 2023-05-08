package com.gg.server.global.exception.custom;

import com.gg.server.global.exception.ErrorCode;

public class AdminException extends CustomRuntimeException {
    private String message;
    private ErrorCode errorCode;

    public AdminException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
