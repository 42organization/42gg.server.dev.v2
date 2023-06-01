package com.gg.server.domain.game.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.DuplicationException;

public class GameAlreadyExistException extends DuplicationException {
    public GameAlreadyExistException() {
        super("게임이 이미 생성되었습니다.", ErrorCode.GAME_DUPLICATION_EXCPETION);
    }
}
