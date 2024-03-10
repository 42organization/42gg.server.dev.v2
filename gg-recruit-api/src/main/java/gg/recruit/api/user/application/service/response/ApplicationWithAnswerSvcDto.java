package gg.recruit.api.user.application.service.response;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import gg.data.recruit.application.ApplicationAnswer;
import gg.data.recruit.application.FormEntityDto;
import lombok.Getter;

@Getter
public class ApplicationWithAnswerSvcDto {
	private Long applicationId;
	private LocalDateTime endTime;
	private String title;
	private String content;
	private List<FormSvcDto> form;

	public ApplicationWithAnswerSvcDto(List<ApplicationAnswer> answers) {
		this.applicationId = answers.get(0).getApplication().getId();
		this.endTime = answers.get(0).getApplication().getRecruit().getEndTime();
		this.title = answers.get(0).getApplication().getRecruit().getTitle();
		this.content = answers.get(0).getApplication().getRecruit().getContents();

		List<FormEntityDto> entityDtos = answers.stream()
			.map(ApplicationAnswer::toForm).collect(toList());

		// groupping answers by questionId and inputType
		Map<Long, List<FormEntityDto>> groupedAnswers = entityDtos.stream()
			.collect(groupingBy(FormEntityDto::getQuestionId));

		// convert to FromSvcDto
		this.form = groupedAnswers.entrySet().stream()
			.map(entry -> {
				Long questionId = entry.getKey();
				List<FormEntityDto> answersByInputType = entry.getValue();
				return new FormSvcDto(questionId, answersByInputType);
			}).collect(toList());
	}
}
