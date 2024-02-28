package gg.pingpong.utils.exception.game;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.InvalidParameterException;

public class NotRecentlyGameException extends InvalidParameterException {
	public NotRecentlyGameException() {
		super(ErrorCode.GAME_NOT_RECENTLY.getMessage(), ErrorCode.GAME_NOT_RECENTLY);
	}
}
