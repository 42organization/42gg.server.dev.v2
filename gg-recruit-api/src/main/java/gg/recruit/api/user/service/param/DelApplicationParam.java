package gg.recruit.api.user.service.param;

import lombok.Getter;

@Getter
public class DelApplicationParam {
	private Long userId;
	private Long applicationId;
	private Long recruitmentId;

	public DelApplicationParam(Long userId, Long applicationId, Long recruitmentId) {
		this.applicationId = applicationId;
		this.recruitmentId = recruitmentId;
		this.userId = userId;
	}
}
