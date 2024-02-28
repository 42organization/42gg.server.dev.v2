package gg.utils.exception.item;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.BusinessException;

public class ItemNotPurchasableException extends BusinessException {
	public ItemNotPurchasableException() {
		super("지금은 구매할 수 없는 아이템 입니다.", ErrorCode.ITEM_NOT_PURCHASABLE);
	}
}
