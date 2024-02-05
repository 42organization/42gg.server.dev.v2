package com.gg.server.domain.user.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class KakaoOauth2NotFoundException extends NotExistException {
	public KakaoOauth2NotFoundException() {
		super("Kakao oauth2 is not found", ErrorCode.KAKAO_OAUTH2_NOT_FOUND);
	}
}
