package com.gg.server.domain.user.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.AuthenticationException;

public class TokenNotValidException extends AuthenticationException {
    public TokenNotValidException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
