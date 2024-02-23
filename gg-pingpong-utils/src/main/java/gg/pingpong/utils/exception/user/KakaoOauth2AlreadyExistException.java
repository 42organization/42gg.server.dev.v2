package gg.pingpong.utils.exception.user;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.InvalidParameterException;

public class KakaoOauth2AlreadyExistException extends InvalidParameterException {
	public KakaoOauth2AlreadyExistException() {
		super("kakao user already exists", ErrorCode.KAKAO_OAUTH2_DUPLICATE);
	}
}
