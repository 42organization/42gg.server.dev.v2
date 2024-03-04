package gg.utils.exception.item;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.BusinessException;

public class ItemTypeException extends BusinessException {
	public ItemTypeException() {
		super("아이템 타입 오류입니다.", ErrorCode.ITEM_TYPE_NOT_MATCHED);
	}
}
