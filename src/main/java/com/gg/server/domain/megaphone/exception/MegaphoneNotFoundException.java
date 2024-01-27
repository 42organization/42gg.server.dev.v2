package com.gg.server.domain.megaphone.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class MegaphoneNotFoundException extends NotExistException {
	public MegaphoneNotFoundException() {
		super("확성기를 찾을 수 없습니다.", ErrorCode.MEGAPHONE_TIME);
	}
}
