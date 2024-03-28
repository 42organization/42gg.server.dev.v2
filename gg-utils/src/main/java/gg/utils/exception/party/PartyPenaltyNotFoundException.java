package gg.utils.exception.party;

import gg.utils.exception.ErrorCode;
import gg.utils.exception.custom.NotExistException;

public class PartyPenaltyNotFoundException extends NotExistException {
	public PartyPenaltyNotFoundException() {
		super(ErrorCode.PARTY_PENALTY_NOT_FOUND.getMessage(), ErrorCode.PARTY_PENALTY_NOT_FOUND);
	}
}
