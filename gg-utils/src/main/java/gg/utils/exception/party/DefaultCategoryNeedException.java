package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class DefaultCategoryNeedException extends NotExistException {
	public static final Long DEFAULT_CATEGORY_ID = 1L;

	public DefaultCategoryNeedException() {
		super(ErrorCode.DEFAULT_CATEGORY_NEED.getMessage(), ErrorCode.DEFAULT_CATEGORY_NEED);
	}
}
