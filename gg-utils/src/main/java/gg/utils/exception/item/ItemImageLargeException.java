package gg.utils.exception.item;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.FileException;

public class ItemImageLargeException extends FileException {
	public ItemImageLargeException() {
		super("이미지 파일 50KB 초과", ErrorCode.USER_IMAGE_TOO_LARGE);
	}
}
