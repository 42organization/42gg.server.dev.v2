package gg.pingpong.utils.exception.custom;

import gg.pingpong.utils.exception.ErrorCode;

public class ForbiddenException extends CustomRuntimeException {
	public ForbiddenException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
