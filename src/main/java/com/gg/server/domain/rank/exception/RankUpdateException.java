package com.gg.server.domain.rank.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.InvalidParameterException;

public class RankUpdateException extends InvalidParameterException {
    public RankUpdateException() {
        super("Ppp를 수정 할 수 없습니다", ErrorCode.RANK_UPDATE_FAIL);
    }
}
