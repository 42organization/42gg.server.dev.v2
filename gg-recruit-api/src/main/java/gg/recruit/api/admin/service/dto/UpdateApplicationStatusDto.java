package gg.recruit.api.admin.service.dto;

import gg.data.recruit.application.enums.ApplicationStatus;
import lombok.Getter;

@Getter
public class UpdateApplicationStatusDto {
	ApplicationStatus status;
	Long applicationId;
	Long recruitId;

	public UpdateApplicationStatusDto(ApplicationStatus status, Long applicationId, Long recruitId) {
		this.status = status;
		this.applicationId = applicationId;
		this.recruitId = recruitId;
	}
}
