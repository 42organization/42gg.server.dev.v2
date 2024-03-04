package gg.utils.exception.custom;

import gg.utils.exception.ErrorCode;

public class AuthenticationException extends CustomRuntimeException {
	private ErrorCode errorCode;

	public AuthenticationException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
