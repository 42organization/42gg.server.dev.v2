package gg.recruit.api.admin.controller.response;

import java.time.LocalDateTime;

import gg.data.recruit.recruitment.Recruitment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentDto {
	private Long id;
	private String title;
	private Boolean isFinish;
	private String generation;
	private LocalDateTime startDate;
	private LocalDateTime endDate;

	@Builder
	public RecruitmentDto(Long id, String title, Boolean isFinish, String generation, LocalDateTime startDate,
		LocalDateTime endDate) {
		this.id = id;
		this.title = title;
		this.isFinish = isFinish;
		this.generation = generation;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public static RecruitmentDto toRecruitmentDto(Recruitment recruitment) {
		return RecruitmentDto.builder()
			.id(recruitment.getId())
			.title(recruitment.getTitle())
			.isFinish(recruitment.getIsFinsh())
			.generation(recruitment.getGeneration())
			.startDate(recruitment.getStartTime())
			.endDate(recruitment.getEndTime())
			.build();
	}
}
