package com.gg.server.domain.user.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.InvalidParameterException;

public class UserAlreadyAttendanceException extends InvalidParameterException {
    public UserAlreadyAttendanceException() {
        super("이미 출석한 유저입니다.", ErrorCode.USER_ALREADY_ATTENDANCE);
    }
}
