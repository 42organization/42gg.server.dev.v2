package com.gg.server.domain.tier.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class TierNotFoundException extends NotExistException {
    public TierNotFoundException() {
        super("해당 티어가 존재하지 않습니다.", ErrorCode.TIER_NOT_FOUND);
    }

}
