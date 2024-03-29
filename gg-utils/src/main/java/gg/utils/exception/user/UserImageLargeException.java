package gg.utils.exception.user;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.FileException;

public class UserImageLargeException extends FileException {
	public UserImageLargeException() {
		super("이미지 파일 50KB 초과", ErrorCode.USER_IMAGE_TOO_LARGE);
	}
}
