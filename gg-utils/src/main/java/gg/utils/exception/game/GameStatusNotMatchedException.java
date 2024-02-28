package gg.utils.exception.game;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.BusinessException;

public class GameStatusNotMatchedException extends BusinessException {
	public GameStatusNotMatchedException() {
		super("게임 상태 오류입니다.", ErrorCode.GAME_STATUS_NOT_MATCHED);
	}
}
