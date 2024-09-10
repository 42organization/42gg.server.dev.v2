package gg.utils.exception.custom;

import gg.utils.exception.ErrorCode;

public class DuplicationException extends CustomRuntimeException {
	public DuplicationException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}

	public DuplicationException(String message) {
		super(message, ErrorCode.CONFLICT);
	}

	public DuplicationException(ErrorCode errorCode) {
		super(errorCode.getMessage(), errorCode);
	}
}
