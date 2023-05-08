package com.gg.server.global.exception.custom;

import com.gg.server.global.exception.ErrorCode;
import lombok.Getter;

@Getter
public class InvalidParameterException extends RuntimeException{
    private ErrorCode errorCode;

    public InvalidParameterException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
        errorCode.setMessage(message);
    }
}
