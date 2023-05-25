package com.gg.server.domain.user.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.FileException;

public class UserImageLargeException extends FileException {
    public UserImageLargeException() {
        super("이미지 파일 50KB 초과", ErrorCode.USER_IMAGE_TOO_LARGE);
    }
}
