package gg.recruit.api.admin.controller.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import gg.recruit.api.admin.service.result.RecruitmentDetailAdminSvcDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecruitmentAdminDetailResDto {
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String title;
	private String contents;
	private String generation;
	private List<FormDetailAdminResDto> forms;

	public RecruitmentAdminDetailResDto(RecruitmentDetailAdminSvcDto recruitmentDetail) {
		this.startDate = recruitmentDetail.getStartDate();
		this.endDate = recruitmentDetail.getEndDate();
		this.title = recruitmentDetail.getTitle();
		this.contents = recruitmentDetail.getContents();
		this.generation = recruitmentDetail.getGeneration();
		this.forms = recruitmentDetail.getForms().stream()
			.map(FormDetailAdminResDto::new).collect(Collectors.toList());
	}
}
