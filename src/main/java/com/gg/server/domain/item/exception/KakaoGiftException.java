package com.gg.server.domain.item.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.BusinessException;

public class KakaoGiftException extends BusinessException {
    public KakaoGiftException() {
        super("카카오 게스트는 선물할 수 없습니다.", ErrorCode.GUEST_ROLE_GIFT_FORBIDDEN);
    }
}
