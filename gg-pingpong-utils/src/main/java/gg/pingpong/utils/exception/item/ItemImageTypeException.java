package gg.pingpong.utils.exception.item;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.FileException;

public class ItemImageTypeException extends FileException {
	public ItemImageTypeException() {
		super("이미지 타입이 올바르지 않습니다", ErrorCode.ITEM_IMAGE_WRONG_TYPE);
	}
}
