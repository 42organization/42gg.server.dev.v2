package gg.recruit.api.user.controller.response;

import static java.util.stream.Collectors.*;

import java.util.List;

import gg.recruit.api.user.service.response.FormSvcDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FormResDto {
	private Long questionId;
	private String inputType;
	private List<CheckListResDto> checkedList;
	private String answer;

	public FormResDto(FormSvcDto formSvcDto) {
		this.questionId = formSvcDto.getQuestionId();
		this.inputType = formSvcDto.getInputType();
		this.checkedList = formSvcDto.getCheckedList().stream()
			.map(CheckListResDto::new).collect(toList());
		this.answer = formSvcDto.getAnswer();
	}
}
