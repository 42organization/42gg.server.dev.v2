package gg.recruit.api.user.controller.response;

import java.time.LocalDateTime;

import gg.data.recruit.application.enums.ApplicationStatus;
import gg.recruit.api.user.service.response.ApplicationResultSvcDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplicationResultResDto {
	private String title;
	private ApplicationStatus status;
	private LocalDateTime interviewDate;

	public ApplicationResultResDto(ApplicationResultSvcDto applicationResult) {
		this.title = applicationResult.getTitle();
		this.status = applicationResult.getStatus();
		this.interviewDate = applicationResult.getInterviewDate();
	}
}
