package gg.party.api.user.penalty.controller.response;

import java.time.LocalDateTime;

import gg.data.party.PartyPenalty;
import lombok.Getter;

@Getter
public class PenaltyResDto {
	private LocalDateTime penaltyEndTime;

	public PenaltyResDto(PartyPenalty partyPenalty) {
		this.penaltyEndTime = partyPenalty.getStartTime().plusMinutes(partyPenalty.getPenaltyTime());
	}

	public PenaltyResDto() {
		this.penaltyEndTime = null;
	}
}
