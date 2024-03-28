package gg.recruit.api.user.service.response;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import gg.data.recruit.application.ApplicationAnswer;
import gg.data.recruit.application.ApplicationAnswerEntityDto;
import lombok.Getter;

@Getter
public class ApplicationWithAnswerSvcDto {
	private final Long applicationId;
	private final LocalDateTime endTime;
	private final String title;
	private final String content;
	private final List<FormSvcDto> form;

	public ApplicationWithAnswerSvcDto(List<ApplicationAnswer> answers) {
		this.applicationId = answers.get(0).getApplication().getId();
		this.endTime = answers.get(0).getApplication().getRecruit().getEndTime();
		this.title = answers.get(0).getApplication().getRecruit().getTitle();
		this.content = answers.get(0).getApplication().getRecruit().getContents();

		List<ApplicationAnswerEntityDto> entityDtos = answers.stream()
			.map(ApplicationAnswer::toForm).collect(toList());

		// groupping answers by questionId and inputType
		Map<Long, List<ApplicationAnswerEntityDto>> groupedAnswers = entityDtos.stream()
			.collect(groupingBy(ApplicationAnswerEntityDto::getQuestionId));

		// convert to FromSvcDto
		this.form = groupedAnswers.entrySet().stream()
			.map(entry -> {
				Long questionId = entry.getKey();
				List<ApplicationAnswerEntityDto> answersByInputType = entry.getValue();
				return new FormSvcDto(questionId, answersByInputType);
			}).collect(toList());
	}
}
