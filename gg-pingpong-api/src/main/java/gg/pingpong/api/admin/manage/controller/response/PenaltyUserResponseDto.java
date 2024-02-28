package gg.pingpong.api.admin.manage.controller.response;

import java.time.LocalDateTime;

import gg.data.manage.Penalty;
import gg.data.manage.redis.RedisPenaltyUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyUserResponseDto {
	private Long penaltyId;
	private String intraId;
	private String reason;
	private LocalDateTime releaseTime;

	public PenaltyUserResponseDto(RedisPenaltyUser penaltyUser) {
		this.intraId = penaltyUser.getIntraId();
		this.reason = penaltyUser.getReason();
		this.releaseTime = penaltyUser.getReleaseTime();
	}

	public PenaltyUserResponseDto(Penalty penalty) {
		this.penaltyId = penalty.getId();
		this.intraId = penalty.getUser().getIntraId();
		this.reason = penalty.getMessage();
		this.releaseTime = penalty.getStartTime().plusMinutes(penalty.getPenaltyTime());
	}

	@Override
	public String toString() {
		return "PenaltyUserResponseDto{"
			+ "penaltyId=" + penaltyId + ", intraId='"
			+ intraId + '\'' + ", reason='"
			+ reason + '\'' + ", releaseTime="
			+ releaseTime
			+ '}';
	}
}
