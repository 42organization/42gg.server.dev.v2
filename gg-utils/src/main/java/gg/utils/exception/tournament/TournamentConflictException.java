package gg.utils.exception.tournament;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.DuplicationException;

public class TournamentConflictException extends DuplicationException {
	public TournamentConflictException() {
		super(ErrorCode.TOURNAMENT_CONFLICT.getMessage(), ErrorCode.TOURNAMENT_CONFLICT);
	}

	public TournamentConflictException(ErrorCode errorCode) {
		super(errorCode.getMessage(), errorCode);
	}
}
