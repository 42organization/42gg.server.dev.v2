package com.gg.server.global.exception.custom;

import com.gg.server.global.exception.ErrorCode;

public class DuplicationException extends CustomRuntimeException {
	public DuplicationException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
