package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.BusinessException;

public class DefaultCategoryNeedException extends BusinessException {
	public static final Long DEFAULT_CATEGORY_ID = 1L;

	public DefaultCategoryNeedException() {
		super(ErrorCode.DEFAULT_CATEGORY_NEED.getMessage(), ErrorCode.DEFAULT_CATEGORY_NEED);
	}
}
