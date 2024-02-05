package com.gg.server.domain.user.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class UserNotFoundException extends NotExistException {
	public UserNotFoundException() {
		super("해당 유저가 없습니다.", ErrorCode.USER_NOT_FOUND);
	}
}
