package gg.party.api.admin.penalty.controller.request;

import java.time.LocalDateTime;

import gg.data.party.PartyPenalty;
import gg.data.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PartyPenaltyAdminReqDto {
	String penaltyType;
	String message;
	int penaltyTime;
	User reportee;

	public PartyPenalty toEntity(User user, String penaltyType, String message, LocalDateTime startTime,
		Integer penaltyTime) {
		return new PartyPenalty(user, penaltyType, message, startTime, penaltyTime);
	}
}
