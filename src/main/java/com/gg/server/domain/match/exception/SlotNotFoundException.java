package com.gg.server.domain.match.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class SlotNotFoundException extends NotExistException {
	public SlotNotFoundException() {
		super("유저가 등록한 슬롯이 없습니다.", ErrorCode.SLOT_NOT_FOUND);
	}
}
