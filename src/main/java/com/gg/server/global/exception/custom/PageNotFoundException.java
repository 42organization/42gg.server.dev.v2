package com.gg.server.global.exception.custom;

import com.gg.server.global.exception.ErrorCode;

public class PageNotFoundException extends CustomRuntimeException {
	public PageNotFoundException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}

	public PageNotFoundException() {
		super(ErrorCode.PAGE_NOT_FOUND.getMessage(), ErrorCode.PAGE_NOT_FOUND);
	}
}
