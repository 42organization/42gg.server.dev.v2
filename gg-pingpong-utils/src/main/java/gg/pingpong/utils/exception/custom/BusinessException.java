package gg.pingpong.utils.exception.custom;

import gg.pingpong.utils.exception.ErrorCode;

public class BusinessException extends CustomRuntimeException {
	public BusinessException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}

	public BusinessException(ErrorCode errorCode) {
		super(errorCode.getMessage(), errorCode);
	}
}
