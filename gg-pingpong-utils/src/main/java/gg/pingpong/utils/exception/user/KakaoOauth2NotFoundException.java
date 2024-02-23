package gg.pingpong.utils.exception.user;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.NotExistException;

public class KakaoOauth2NotFoundException extends NotExistException {
	public KakaoOauth2NotFoundException() {
		super("Kakao oauth2 is not found", ErrorCode.KAKAO_OAUTH2_NOT_FOUND);
	}
}
