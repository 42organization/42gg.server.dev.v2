package com.gg.server.domain.coin.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.CustomRuntimeException;

public class CoinHistoryNotFoundException extends CustomRuntimeException {
    public CoinHistoryNotFoundException() {
        super(ErrorCode.COIN_HISTORY_NOT_FOUND.getMessage(), ErrorCode.COIN_HISTORY_NOT_FOUND);
    }
}
