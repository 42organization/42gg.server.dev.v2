package gg.pingpong.utils.exception.team;

import gg.pingpong.utils.exception.ErrorCode;
import gg.pingpong.utils.exception.custom.NotExistException;

public class TeamNotFoundException extends NotExistException {

	public TeamNotFoundException() {
		super(ErrorCode.TEAM_NOT_FOUND.getMessage(), ErrorCode.TEAM_NOT_FOUND);
	}
}
