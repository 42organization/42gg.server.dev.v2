package gg.recruit.api.user.controller.response;

import java.time.LocalDateTime;

import gg.recruit.api.user.service.response.ApplicationResultSvcDto;
import lombok.Getter;

@Getter
public class ApplicationResultResDto {
	private String title;
	private String status;
	private LocalDateTime interviewDate;

	public ApplicationResultResDto(ApplicationResultSvcDto applicationResult) {
		this.title = applicationResult.getTitle();
		this.status = applicationResult.getStatus().name();
		this.interviewDate = applicationResult.getInterviewDate();
	}
}
