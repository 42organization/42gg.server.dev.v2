package com.gg.server.domain.game.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.BusinessException;

public class GameStatusNotMatchedException extends BusinessException {
	public GameStatusNotMatchedException() {
		super("게임 상태 오류입니다.", ErrorCode.GAME_STATUS_NOT_MATCHED);
	}
}
