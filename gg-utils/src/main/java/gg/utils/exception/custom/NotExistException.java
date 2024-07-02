package gg.utils.exception.custom;

import gg.utils.exception.ErrorCode;

public class NotExistException extends CustomRuntimeException {
	private ErrorCode errorCode;

	public NotExistException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}

	public NotExistException(String message) {
		super(message, ErrorCode.NOT_FOUND);
	}

	public NotExistException(ErrorCode errorCode) {
		super(errorCode.getMessage(), ErrorCode.NOT_FOUND);
	}
}
