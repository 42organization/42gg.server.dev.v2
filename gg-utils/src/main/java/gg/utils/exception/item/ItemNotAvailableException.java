package gg.utils.exception.item;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.FileException;

public class ItemNotAvailableException extends FileException {
	public ItemNotAvailableException() {
		super("아이템 접근이 불가합니다", ErrorCode.ITEM_NOT_AVAILABLE);
	}
}
