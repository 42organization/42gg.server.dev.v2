package com.gg.server.domain.user.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.InvalidParameterException;
import com.gg.server.global.exception.custom.NotExistException;

public class KakaoOauth2AlreadyExistException extends InvalidParameterException {
    public KakaoOauth2AlreadyExistException() {
        super("kakao user already exists", ErrorCode.KAKAO_OAUTH2_DUPLICATE);
    }
}
