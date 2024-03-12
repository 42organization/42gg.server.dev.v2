package gg.party.api.admin.penalty.requset;

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

	public PartyPenaltyAdminReqDto(String penaltyType, String message, int penaltyTime) {
		this.penaltyType = penaltyType;
		this.message = message;
		this.penaltyTime = penaltyTime;
	}

	public PartyPenalty toEntity(User user, String penaltyType, String message, LocalDateTime startTime,
		Integer penaltyTime) {
		return new PartyPenalty(user, penaltyType, message, startTime, penaltyTime);
	}
}
