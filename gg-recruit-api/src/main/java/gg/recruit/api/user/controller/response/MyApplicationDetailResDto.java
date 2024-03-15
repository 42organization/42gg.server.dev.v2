package gg.recruit.api.user.controller.response;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.List;

import gg.recruit.api.user.service.response.ApplicationWithAnswerSvcDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MyApplicationDetailResDto {
	private Long applicationId;
	private LocalDateTime endTime;
	private String title;
	private String content;
	private List<FormResDto> from;

	public MyApplicationDetailResDto(ApplicationWithAnswerSvcDto applicationWithAnswerSvcDto) {
		this.applicationId = applicationWithAnswerSvcDto.getApplicationId();
		this.endTime = applicationWithAnswerSvcDto.getEndTime();
		this.title = applicationWithAnswerSvcDto.getTitle();
		this.content = applicationWithAnswerSvcDto.getContent();
		this.from = applicationWithAnswerSvcDto.getForm().stream()
			.map(FormResDto::new)
			.collect(toList());
	}
}
