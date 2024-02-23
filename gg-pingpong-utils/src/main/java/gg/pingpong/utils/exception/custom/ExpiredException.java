package gg.pingpong.utils.exception.custom;

import gg.pingpong.utils.exception.ErrorCode;

public class ExpiredException extends CustomRuntimeException {
	public ExpiredException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
