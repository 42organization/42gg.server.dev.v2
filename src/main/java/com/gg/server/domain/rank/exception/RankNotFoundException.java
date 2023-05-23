package com.gg.server.domain.rank.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class RankNotFoundException extends NotExistException {
    public RankNotFoundException() {
        super("랭크 테이블에 없는 유저입니다.", ErrorCode.RANK_NOT_FOUND);
    }
}
