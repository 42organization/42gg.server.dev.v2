package com.gg.server.domain.team.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class TeamIdNotMatchException extends NotExistException {
    public TeamIdNotMatchException() {
        super("Team Id가 일치하지 않습니다.", ErrorCode.NOT_FOUND);
    }
}
