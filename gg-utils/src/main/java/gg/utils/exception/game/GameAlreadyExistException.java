package gg.utils.exception.game;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.DuplicationException;

public class GameAlreadyExistException extends DuplicationException {
	public GameAlreadyExistException() {
		super("게임이 이미 생성되었습니다.", ErrorCode.GAME_DUPLICATION_EXCEPTION);
	}
}
