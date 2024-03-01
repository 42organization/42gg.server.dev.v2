package gg.utils.exception.user;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.InvalidParameterException;

public class KakaoOauth2AlreadyExistException extends InvalidParameterException {
	public KakaoOauth2AlreadyExistException() {
		super("kakao user already exists", ErrorCode.KAKAO_OAUTH2_DUPLICATE);
	}
}
