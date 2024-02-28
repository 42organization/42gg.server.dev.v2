package gg.utils.exception.game;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.InvalidParameterException;

public class NotRecentlyGameException extends InvalidParameterException {
	public NotRecentlyGameException() {
		super(ErrorCode.GAME_NOT_RECENTLY.getMessage(), ErrorCode.GAME_NOT_RECENTLY);
	}
}
