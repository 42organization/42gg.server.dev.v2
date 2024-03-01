package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class CategoryNotFoundException extends NotExistException {
	public CategoryNotFoundException() {
		super("해당 카테고리가 없습니다.", ErrorCode.CATEGORY_NOT_FOUND);
	}

}
