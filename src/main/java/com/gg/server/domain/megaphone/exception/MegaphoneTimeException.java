package com.gg.server.domain.megaphone.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.BusinessException;

public class MegaphoneTimeException extends BusinessException {
    public MegaphoneTimeException() {
        super("확성기 사용이 불가능한 시간입니다.", ErrorCode.MEGAPHONE_TIME);
    }
}
