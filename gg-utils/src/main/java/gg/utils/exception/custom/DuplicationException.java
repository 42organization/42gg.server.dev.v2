package gg.utils.exception.custom;

import gg.utils.exception.ErrorCode;

public class DuplicationException extends CustomRuntimeException {
	public DuplicationException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
