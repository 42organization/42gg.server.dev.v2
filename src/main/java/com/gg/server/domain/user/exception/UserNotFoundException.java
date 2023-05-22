package com.gg.server.domain.user.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class UserNotFoundException extends NotExistException {
    public UserNotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
