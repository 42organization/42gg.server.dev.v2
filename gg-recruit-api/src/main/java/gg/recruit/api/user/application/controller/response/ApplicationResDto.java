package gg.recruit.api.user.application.controller.response;

import java.time.LocalDateTime;

import gg.data.recruit.application.Application;
import gg.recruit.api.user.application.service.response.ApplicationDto;
import lombok.Getter;

@Getter
public class ApplicationResDto {
	private Long recruitId;
	private Long applicationId;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String title;
	private String generation;
	private String status;

	public ApplicationResDto(ApplicationDto applicationDto) {
		this.recruitId = applicationDto.getRecruitId();
		this.applicationId = applicationDto.getApplicationId();
		this.startDate = applicationDto.getStartDate();

	}
}
