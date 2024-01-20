package com.gg.server.domain.tournament.exception;

import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.exception.custom.NotExistException;

public class TournamentGameNotFoundException extends NotExistException {
	public TournamentGameNotFoundException() {
		super(ErrorCode.TOURNAMENT_GAME_NOT_FOUND.getMessage(), ErrorCode.TOURNAMENT_GAME_NOT_FOUND);
	}
}
