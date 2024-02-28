package gg.pingpong.utils.exception.custom;

import gg.pingpong.utils.exception.ErrorCode;

public class AuthenticationException extends CustomRuntimeException {
	private ErrorCode errorCode;

	public AuthenticationException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
