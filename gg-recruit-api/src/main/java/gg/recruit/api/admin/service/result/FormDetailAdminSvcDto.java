package gg.recruit.api.admin.service.result;

import java.util.List;
import java.util.stream.Collectors;

import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.enums.InputType;
import lombok.Getter;

@Getter
public class FormDetailAdminSvcDto {
	private Long questionId;
	private String question;
	private InputType inputType;
	private List<CheckItemAdminSvcDto> checkList;

	public FormDetailAdminSvcDto(Question question) {
		this.questionId = question.getId();
		this.question = question.getQuestion();
		this.inputType = question.getInputType();
		this.checkList = question.getCheckLists().stream()
			.map(CheckItemAdminSvcDto::new).collect(Collectors.toList());
	}
}
