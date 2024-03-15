package gg.utils.exception.party;

import org.webjars.NotFoundException;

import gg.utils.exception.ErrorCode;

public class CategoryNotFoundException extends NotFoundException {
	public CategoryNotFoundException() {
		super(ErrorCode.CATEGORY_NOT_FOUND.getMessage());
	}
}
