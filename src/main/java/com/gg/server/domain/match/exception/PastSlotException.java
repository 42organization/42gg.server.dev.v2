package com.gg.server.domain.match.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.ExpiredException;

public class PastSlotException extends ExpiredException {
	public PastSlotException() {
		super("현재 시각 이전 슬롯은 등록할 수 없습니다.", ErrorCode.SLOT_PAST);
	}
}
