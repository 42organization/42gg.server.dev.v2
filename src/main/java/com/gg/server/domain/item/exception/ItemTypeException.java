package com.gg.server.domain.item.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.BusinessException;

public class ItemTypeException extends BusinessException {
	public ItemTypeException() {
		super("아이템 타입 오류입니다.", ErrorCode.ITEM_TYPE_NOT_MATCHED);
	}
}
