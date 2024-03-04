package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.InvalidParameterException;

public class CommentNotValidException extends InvalidParameterException {
	public CommentNotValidException() {
		super(ErrorCode.COMMENT_TOO_LONG.getMessage(), ErrorCode.COMMENT_TOO_LONG);
	}
}
