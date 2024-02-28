package gg.utils.exception.item;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class ItemNotFoundException extends NotExistException {
	public ItemNotFoundException() {
		super("아이템이 없습니다.", ErrorCode.ITEM_NOT_FOUND);
	}
}
