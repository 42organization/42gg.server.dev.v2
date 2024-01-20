package com.gg.server.global.exception.custom;

import com.gg.server.global.exception.ErrorCode;

public class ForbiddenException extends CustomRuntimeException {
	public ForbiddenException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
