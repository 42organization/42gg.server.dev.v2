package gg.recruit.api.user.service.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.Recruitments;
import lombok.Getter;

@Getter
public class RecruitmentDetailSvcDto {
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private String title;
	private String contents;
	private String generation;
	private List<FormDetailSvcDto> forms;

	public RecruitmentDetailSvcDto(Recruitments recruit, List<Question> questions) {
		this.startDate = recruit.getStartTime();
		this.endDate = recruit.getEndTime();
		this.title = recruit.getTitle();
		this.contents = recruit.getContents();
		this.generation = recruit.getGeneration();
		this.forms = questions.stream().map(FormDetailSvcDto::new).collect(Collectors.toList());
	}
}
