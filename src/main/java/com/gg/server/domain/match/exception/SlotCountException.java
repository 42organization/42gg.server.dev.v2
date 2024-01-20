package com.gg.server.domain.match.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.BusinessException;

public class SlotCountException extends BusinessException {
	public SlotCountException() {
		super("슬롯 등록 횟수 3회를 초과했습니다.", ErrorCode.SLOT_COUNT_EXCEEDED);
	}
}
