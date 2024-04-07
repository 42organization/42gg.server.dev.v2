package gg.recruit.api.admin.service.result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import gg.data.recruit.recruitment.Recruitment;
import lombok.Getter;

@Getter
public class RecruitmentDetailAdminSvcDto {
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String title;
	private String contents;
	private String generation;
	private List<FormDetailAdminSvcDto> forms;

	public RecruitmentDetailAdminSvcDto(Recruitment recruit) {
		this.startDate = recruit.getStartTime();
		this.endDate = recruit.getEndTime();
		this.title = recruit.getTitle();
		this.contents = recruit.getContents();
		this.generation = recruit.getGeneration();
		this.forms = recruit.getQuestions().stream()
			.map(FormDetailAdminSvcDto::new).collect(Collectors.toList());
	}
}
