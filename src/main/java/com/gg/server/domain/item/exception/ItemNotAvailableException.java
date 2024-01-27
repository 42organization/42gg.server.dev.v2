package com.gg.server.domain.item.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.FileException;

public class ItemNotAvailableException extends FileException {
	public ItemNotAvailableException() {
		super("아이템 접근이 불가합니다", ErrorCode.ITEM_NOT_AVAILABLE);
	}
}
