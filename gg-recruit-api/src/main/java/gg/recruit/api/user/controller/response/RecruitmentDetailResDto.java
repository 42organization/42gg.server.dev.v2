package gg.recruit.api.user.controller.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import gg.recruit.api.user.service.response.RecruitmentDetailSvcDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class RecruitmentDetailResDto {
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String title;
	private String contents;
	private String generation;
	private List<FormDetailResDto> forms;
	private Long applicationId;

	public RecruitmentDetailResDto(RecruitmentDetailSvcDto dto, Long applicationId) {
		this.startDate = dto.getStartDate();
		this.endDate = dto.getEndDate();
		this.title = dto.getTitle();
		this.contents = dto.getContents();
		this.generation = dto.getGeneration();
		this.forms = dto.getForms().stream().map(FormDetailResDto::new).collect(Collectors.toList());
		this.applicationId = applicationId;
	}
}
