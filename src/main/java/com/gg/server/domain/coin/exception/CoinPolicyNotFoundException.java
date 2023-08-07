package com.gg.server.domain.coin.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.CustomRuntimeException;

public class CoinPolicyNotFoundException extends CustomRuntimeException {
    public CoinPolicyNotFoundException() {
        super(ErrorCode.CoinPolicy_NOT_FOUND.getMessage(), ErrorCode.CoinPolicy_NOT_FOUND);
    }
}
