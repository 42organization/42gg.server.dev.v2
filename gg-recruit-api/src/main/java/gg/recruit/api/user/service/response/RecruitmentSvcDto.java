package gg.recruit.api.user.service.response;

import java.time.LocalDateTime;

import gg.data.recruit.recruitment.Recruitments;
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

	public RecruitmentSvcDto(Recruitments recruitments) {
		this.id = recruitments.getId();
		this.startDate = recruitments.getStartTime();
		this.endDate = recruitments.getEndTime();
		this.title = recruitments.getTitle();
		this.generation = recruitments.getGeneration();
	}
}
