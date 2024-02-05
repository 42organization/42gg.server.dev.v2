package com.gg.server.domain.match.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.InvalidParameterException;

public class OptionInvalidException extends InvalidParameterException {
	public OptionInvalidException() {
		super("존재하지 않은 mode 입니다", ErrorCode.MODE_INVALID);
	}
}
