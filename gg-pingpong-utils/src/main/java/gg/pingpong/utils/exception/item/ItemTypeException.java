package gg.pingpong.utils.exception.item;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.BusinessException;

public class ItemTypeException extends BusinessException {
	public ItemTypeException() {
		super("아이템 타입 오류입니다.", ErrorCode.ITEM_TYPE_NOT_MATCHED);
	}
}
