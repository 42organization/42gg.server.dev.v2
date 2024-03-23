package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class CategoryNotFoundException extends NotExistException {
	public CategoryNotFoundException() {
		super(ErrorCode.CATEGORY_NOT_FOUND.getMessage(), ErrorCode.CATEGORY_NOT_FOUND);
	}
}
