package com.gg.server.domain.user.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.FileException;

public class UserImageNullException extends FileException {
	public UserImageNullException() {
		super("이미지 파일이 없습니다.", ErrorCode.USER_IMAGE_NOT_FOUND);
	}
}
