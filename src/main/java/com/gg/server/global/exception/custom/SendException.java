package com.gg.server.global.exception.custom;

import com.gg.server.global.exception.ErrorCode;

public class SendException extends CustomRuntimeException {
	public SendException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
