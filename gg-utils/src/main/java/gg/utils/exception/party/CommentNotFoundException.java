package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class CommentNotFoundException extends NotExistException {
	public CommentNotFoundException() {
		super(ErrorCode.COMMENT_NOT_FOUND.getMessage(), ErrorCode.COMMENT_NOT_FOUND);
	}

}
