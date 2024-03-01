package gg.utils.exception.custom;

import gg.utils.exception.ErrorCode;

public class FileException extends CustomRuntimeException {

	public FileException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
