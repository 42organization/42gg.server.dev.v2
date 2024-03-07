package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.DuplicationException;

public class CategoryDuplicateException extends DuplicationException {
	public CategoryDuplicateException() {
		super(ErrorCode.CATEGORY_DUPLICATE.getMessage(), ErrorCode.CATEGORY_DUPLICATE);
	}
}
