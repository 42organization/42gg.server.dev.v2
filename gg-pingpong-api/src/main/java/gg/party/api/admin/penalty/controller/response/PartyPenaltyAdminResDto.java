package gg.party.api.admin.penalty.controller.response;

import java.time.LocalDateTime;

import gg.data.party.PartyPenalty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class PartyPenaltyAdminResDto {
	private Long id;
	private String userIntraId;
	private String penaltyType;
	private String message;
	private LocalDateTime startTime;
	private Integer penaltyTime;

	public PartyPenaltyAdminResDto(PartyPenalty partyPenalty) {
		this.id = partyPenalty.getId();
		this.userIntraId = partyPenalty.getUser().getIntraId();
		this.penaltyType = partyPenalty.getPenaltyType();
		this.message = partyPenalty.getMessage();
		this.startTime = partyPenalty.getStartTime();
		this.penaltyTime = partyPenalty.getPenaltyTime();
	}
}
