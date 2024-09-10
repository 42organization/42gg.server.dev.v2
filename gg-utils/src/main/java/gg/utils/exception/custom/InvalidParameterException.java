package gg.utils.exception.custom;

import gg.utils.exception.ErrorCode;

public class InvalidParameterException extends CustomRuntimeException {
	public InvalidParameterException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}

	public InvalidParameterException(ErrorCode errorCode) {
		super(errorCode);
	}
}
