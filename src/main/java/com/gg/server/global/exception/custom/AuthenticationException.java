package com.gg.server.global.exception.custom;

import com.gg.server.global.exception.ErrorCode;

public class AuthenticationException extends CustomRuntimeException {
	private ErrorCode errorCode;

	public AuthenticationException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
