package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class TemplateNotFoundException extends NotExistException {
	public TemplateNotFoundException() {
		super(ErrorCode.TEMPLATE_NOT_FOUND.getMessage(), ErrorCode.TEMPLATE_NOT_FOUND);
	}
}
