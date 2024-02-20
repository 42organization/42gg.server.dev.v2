package gg.pingpong.utils.exception.item;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.FileException;

public class ItemImageLargeException extends FileException {
	public ItemImageLargeException() {
		super("이미지 파일 50KB 초과", ErrorCode.USER_IMAGE_TOO_LARGE);
	}
}
