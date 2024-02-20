package gg.pingpong.utils.exception.item;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.FileException;

public class ItemNotAvailableException extends FileException {
	public ItemNotAvailableException() {
		super("아이템 접근이 불가합니다", ErrorCode.ITEM_NOT_AVAILABLE);
	}
}
