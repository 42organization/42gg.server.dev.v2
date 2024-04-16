package gg.party.api.admin.penalty.controller.request;

import java.time.LocalDateTime;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import gg.data.party.PartyPenalty;
import gg.data.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PartyPenaltyAdminReqDto {
	@NotBlank(message = "Penalty type이 비어있습니다")
	@Size(max = 20, message = "Penalty type은 최대 20자입니다")
	String penaltyType;

	@NotBlank(message = "message가 비어있습니다")
	@Size(max = 100, message = "message는 최대 100자입니다")
	String message;

	@NotNull(message = "penaltyTime이 비어있습니다")
	@Min(value = 0, message = "올바른 penaltyTime을 넣어주세요")
	int penaltyTime;

	@NotBlank(message = "IntraId가 비어있습니다")
	String userIntraId;

	public PartyPenalty toEntity(User user, String penaltyType, String message, LocalDateTime startTime,
		Integer penaltyTime) {
		return new PartyPenalty(user, penaltyType, message, startTime, penaltyTime);
	}

	public PartyPenaltyAdminReqDto(String penaltyType, String message, int penaltyTime, String userIntraId) {
		this.penaltyType = penaltyType;
		this.message = message;
		this.penaltyTime = penaltyTime;
		this.userIntraId = userIntraId;
	}
}
