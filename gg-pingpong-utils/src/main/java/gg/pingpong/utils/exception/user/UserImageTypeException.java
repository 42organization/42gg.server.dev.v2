package gg.pingpong.utils.exception.user;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.FileException;

public class UserImageTypeException extends FileException {
	public UserImageTypeException() {
		super("이미지 타입이 올바르지 않습니다", ErrorCode.USER_IMAGE_WRONG_TYPE);
	}
}
