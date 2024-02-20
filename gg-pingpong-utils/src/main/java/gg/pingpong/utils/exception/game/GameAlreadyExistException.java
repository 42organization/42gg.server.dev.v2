package gg.pingpong.utils.exception.game;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.DuplicationException;

public class GameAlreadyExistException extends DuplicationException {
	public GameAlreadyExistException() {
		super("게임이 이미 생성되었습니다.", ErrorCode.GAME_DUPLICATION_EXCEPTION);
	}
}
