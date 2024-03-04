package gg.utils.exception.user;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class KakaoOauth2NotFoundException extends NotExistException {
	public KakaoOauth2NotFoundException() {
		super("Kakao oauth2 is not found", ErrorCode.KAKAO_OAUTH2_NOT_FOUND);
	}
}
