package com.gg.server.domain.user.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.CustomRuntimeException;

public class UserTextColorException extends CustomRuntimeException {
    public UserTextColorException() {
        super("user text color code is not valid", ErrorCode.USER_TEXT_COLOR_WRONG_TYPE);
    }
}
