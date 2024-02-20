package gg.pingpong.utils.exception.game;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.NotExistException;

public class GameNotExistException extends NotExistException {
	public GameNotExistException() {
		super("game 이 존재하지 않습니다.", ErrorCode.GAME_NOT_FOUND);
	}
}
