package com.gg.server.domain.penalty.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.ExpiredException;

public class PenaltyExpiredException extends ExpiredException {
    public PenaltyExpiredException() {
        super("이미 만료된 패널티입니다.", ErrorCode.PENALTY_EXPIRED);
    }
}
