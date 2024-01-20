package com.gg.server.domain.item.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.BusinessException;

public class ItemNotPurchasableException extends BusinessException {
	public ItemNotPurchasableException() {
		super("지금은 구매할 수 없는 아이템 입니다.", ErrorCode.ITEM_NOT_PURCHASABLE);
	}
}
