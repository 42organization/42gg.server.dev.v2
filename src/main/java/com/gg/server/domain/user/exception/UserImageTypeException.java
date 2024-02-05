package com.gg.server.domain.user.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.FileException;

public class UserImageTypeException extends FileException {
	public UserImageTypeException() {
		super("이미지 타입이 올바르지 않습니다", ErrorCode.USER_IMAGE_WRONG_TYPE);
	}
}
