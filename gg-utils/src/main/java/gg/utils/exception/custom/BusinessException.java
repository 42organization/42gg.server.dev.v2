package gg.utils.exception.custom;

import gg.utils.exception.ErrorCode;

public class BusinessException extends CustomRuntimeException {
	public BusinessException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}

	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage(), errorCode);
	}

	public BusinessException(String message) {
		super(message, ErrorCode.BAD_REQUEST);
	}
}
