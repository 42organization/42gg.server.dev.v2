package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.DuplicationException;

public class PenaltyNotFoundException extends DuplicationException {
	public PenaltyNotFoundException() {
		super(ErrorCode.PARTY_PENALTY_NOT_FOUND.getMessage(), ErrorCode.PARTY_PENALTY_NOT_FOUND);
	}
}
