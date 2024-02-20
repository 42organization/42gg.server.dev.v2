package gg.pingpong.utils.exception.team;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.NotExistException;

public class TeamIdNotMatchException extends NotExistException {
	public TeamIdNotMatchException() {
		super(ErrorCode.TEAM_ID_NOT_MATCH.getMessage(), ErrorCode.TEAM_ID_NOT_MATCH);
	}
}
