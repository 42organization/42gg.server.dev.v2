package gg.utils.exception.custom;

import gg.utils.exception.ErrorCode;

public class ForbiddenException extends CustomRuntimeException {
	public ForbiddenException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}

	public ForbiddenException(String message) {
		super(message, ErrorCode.FORBIDDEN);
	}
}
