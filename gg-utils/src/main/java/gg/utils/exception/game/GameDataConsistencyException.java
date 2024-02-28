package gg.utils.exception.game;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.DBConsistencyException;

public class GameDataConsistencyException extends DBConsistencyException {
	public GameDataConsistencyException() {
		super("game db 가 올바르지 않습니다.", ErrorCode.GAME_DB_NOT_VALID);
	}
}
