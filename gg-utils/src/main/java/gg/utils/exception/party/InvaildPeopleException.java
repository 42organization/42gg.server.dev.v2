package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.ForbiddenException;

public class InvaildPeopleException extends ForbiddenException {
	public InvaildPeopleException() {
		super(ErrorCode.NOT_HOST.getMessage(), ErrorCode.NOT_HOST);
	}
}
