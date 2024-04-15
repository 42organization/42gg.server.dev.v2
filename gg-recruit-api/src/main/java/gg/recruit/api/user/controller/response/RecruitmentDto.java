package gg.recruit.api.user.controller.response;

import java.time.LocalDateTime;

import gg.recruit.api.user.service.response.RecruitmentSvcDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecruitmentDto {
	private Long id;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String title;
	private String generation;

	public RecruitmentDto(RecruitmentSvcDto dto) {
		this.id = dto.getId();
		this.startDate = dto.getStartDate();
		this.endDate = dto.getEndDate();
		this.title = dto.getTitle();
		this.generation = dto.getGeneration();
	}
}
