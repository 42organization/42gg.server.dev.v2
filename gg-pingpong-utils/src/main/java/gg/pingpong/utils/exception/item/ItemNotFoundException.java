package gg.pingpong.utils.exception.item;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.NotExistException;

public class ItemNotFoundException extends NotExistException {
	public ItemNotFoundException() {
		super("아이템이 없습니다.", ErrorCode.ITEM_NOT_FOUND);
	}
}
