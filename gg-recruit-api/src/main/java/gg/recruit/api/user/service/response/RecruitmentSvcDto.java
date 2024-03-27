package gg.recruit.api.user.service.response;

import java.time.LocalDateTime;

import gg.data.recruit.recruitment.Recruitment;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecruitmentSvcDto {
	private Long id;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String title;
	private String generation;

	public RecruitmentSvcDto(Recruitment recruitments) {
		this.id = recruitments.getId();
		this.startDate = recruitments.getStartTime();
		this.endDate = recruitments.getEndTime();
		this.title = recruitments.getTitle();
		this.generation = recruitments.getGeneration();
	}
}
