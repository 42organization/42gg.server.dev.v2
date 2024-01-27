package com.gg.server.domain.team.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class TeamIdNotMatchException extends NotExistException {
	public TeamIdNotMatchException() {
		super(ErrorCode.TEAM_ID_NOT_MATCH.getMessage(), ErrorCode.TEAM_ID_NOT_MATCH);
	}
}
