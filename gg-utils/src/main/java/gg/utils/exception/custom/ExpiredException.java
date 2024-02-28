package gg.utils.exception.custom;

import gg.utils.exception.ErrorCode;

public class ExpiredException extends CustomRuntimeException {
	public ExpiredException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
