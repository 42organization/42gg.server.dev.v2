package com.gg.server.global.exception.custom;

import com.gg.server.global.exception.ErrorCode;

public class FileException extends CustomRuntimeException {

	public FileException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
