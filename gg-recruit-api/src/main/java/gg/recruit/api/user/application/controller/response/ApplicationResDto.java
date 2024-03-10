package gg.recruit.api.user.application.controller.response;

import java.time.LocalDateTime;

import gg.recruit.api.user.application.service.response.ApplicationSvcDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApplicationResDto {
	private Long recruitId;
	private Long applicationId;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String title;
	private String generation;
	private String status;

	public ApplicationResDto(ApplicationSvcDto applicationDto) {
		this.recruitId = applicationDto.getRecruitId();
		this.applicationId = applicationDto.getApplicationId();
		this.startDate = applicationDto.getStartDate();
		this.endDate = applicationDto.getEndDate();
		this.title = applicationDto.getTitle();
		this.generation = applicationDto.getGeneration();
		this.status = applicationDto.getStatus();
	}
}
