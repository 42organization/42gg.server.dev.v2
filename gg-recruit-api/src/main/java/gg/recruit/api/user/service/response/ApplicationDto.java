package gg.recruit.api.user.service.response;

import java.time.LocalDateTime;

import gg.data.recruit.application.Application;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApplicationDto {
	private Long recruitId;
	private Long applicationId;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String title;
	private String generation;
	private String status;

	public ApplicationDto(Application application) {
		this.recruitId = application.getRecruit().getId();
		this.applicationId = application.getId();
		this.startDate = application.getRecruit().getStartTime();
		this.endDate = application.getRecruit().getEndTime();
		this.title = application.getRecruit().getTitle();
		this.generation = application.getRecruit().getGeneration();
		this.status = application.getStatus().name();
	}
}
