package com.gg.server.domain.game.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.CustomRuntimeException;

public class GameDataException extends CustomRuntimeException {
    public GameDataException() {
        super("game data 가 잘못되었습니다.", ErrorCode.VALID_FAILED);
    }
}
