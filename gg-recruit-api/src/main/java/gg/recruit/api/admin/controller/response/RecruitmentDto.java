package gg.recruit.api.admin.controller.response;

import java.time.LocalDateTime;

import gg.data.recruit.recruitment.Recruitment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecruitmentDto {
	private Long id;
	private String title;
	private String status;
	private String generation;
	private LocalDateTime startDate;
	private LocalDateTime endDate;

	@Builder
	public RecruitmentDto(Long id, String title, String status, String generation, LocalDateTime startDate,
		LocalDateTime endDate) {
		this.id = id;
		this.title = title;
		this.status = status;
		this.generation = generation;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public static RecruitmentDto toRecruitmentDto(Recruitment recruitment) {
		return RecruitmentDto.builder()
			.id(recruitment.getId())
			.title(recruitment.getTitle())
			.generation(recruitment.getGeneration())
			.startDate(recruitment.getStartTime())
			.endDate(recruitment.getEndTime())
			.build();
	}
}
