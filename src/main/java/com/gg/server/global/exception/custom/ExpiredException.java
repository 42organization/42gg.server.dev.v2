package com.gg.server.global.exception.custom;

import com.gg.server.global.exception.ErrorCode;

public class ExpiredException extends CustomRuntimeException {
	public ExpiredException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
