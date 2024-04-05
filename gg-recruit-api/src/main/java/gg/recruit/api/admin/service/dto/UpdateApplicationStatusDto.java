package gg.recruit.api.admin.service.dto;

import java.time.LocalDateTime;

import gg.data.recruit.application.enums.ApplicationStatus;
import lombok.Getter;

@Getter
public class UpdateApplicationStatusDto {
	ApplicationStatus status;
	Long applicationId;
	Long recruitId;
	LocalDateTime interviewDate;

	public UpdateApplicationStatusDto(ApplicationStatus status, Long applicationId, Long recruitId) {
		this.status = status;
		this.applicationId = applicationId;
		this.recruitId = recruitId;
	}

	public UpdateApplicationStatusDto(ApplicationStatus status, Long applicationId, Long recruitId,
		LocalDateTime interviewDate) {
		this.status = status;
		this.applicationId = applicationId;
		this.recruitId = recruitId;
		this.interviewDate = interviewDate;
	}
}
