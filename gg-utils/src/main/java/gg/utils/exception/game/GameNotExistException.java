package gg.utils.exception.game;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class GameNotExistException extends NotExistException {
	public GameNotExistException() {
		super("game 이 존재하지 않습니다.", ErrorCode.GAME_NOT_FOUND);
	}
}
