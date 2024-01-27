package com.gg.server.domain.game.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.DBConsistencyException;

public class GameDataConsistencyException extends DBConsistencyException {
	public GameDataConsistencyException() {
		super("game db 가 올바르지 않습니다.", ErrorCode.GAME_DB_NOT_VALID);
	}
}
