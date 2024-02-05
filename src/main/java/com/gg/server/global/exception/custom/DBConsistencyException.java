package com.gg.server.global.exception.custom;

import com.gg.server.global.exception.ErrorCode;

import lombok.Getter;

@Getter
public class DBConsistencyException extends CustomRuntimeException {
	public DBConsistencyException(String message, ErrorCode errorCode) {
		super(message, errorCode);
	}
}
