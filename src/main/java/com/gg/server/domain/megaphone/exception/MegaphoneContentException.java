package com.gg.server.domain.megaphone.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.BusinessException;

public class MegaphoneContentException extends BusinessException {
	public MegaphoneContentException() {
		super("확성기 내용이 없습니다.", ErrorCode.MEGAPHONE_CONTENT);
	}
}
