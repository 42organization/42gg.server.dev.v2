package com.gg.server.global.exception.custom;

import com.gg.server.global.exception.ErrorCode;

public class ParseException extends CustomRuntimeException {
    public ParseException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
