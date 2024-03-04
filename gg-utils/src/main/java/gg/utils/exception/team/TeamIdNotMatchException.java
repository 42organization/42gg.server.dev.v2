package gg.utils.exception.team;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class TeamIdNotMatchException extends NotExistException {
	public TeamIdNotMatchException() {
		super(ErrorCode.TEAM_ID_NOT_MATCH.getMessage(), ErrorCode.TEAM_ID_NOT_MATCH);
	}
}
