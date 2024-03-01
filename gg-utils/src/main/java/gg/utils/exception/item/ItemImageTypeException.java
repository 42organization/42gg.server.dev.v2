package gg.utils.exception.item;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.FileException;

public class ItemImageTypeException extends FileException {
	public ItemImageTypeException() {
		super("이미지 타입이 올바르지 않습니다", ErrorCode.ITEM_IMAGE_WRONG_TYPE);
	}
}
