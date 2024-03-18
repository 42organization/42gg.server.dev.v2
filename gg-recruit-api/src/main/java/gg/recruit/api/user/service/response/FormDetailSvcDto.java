package gg.recruit.api.user.service.response;

import java.util.List;
import java.util.stream.Collectors;

import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.enums.InputType;
import lombok.Getter;

@Getter
public class FormDetailSvcDto {
	private Long questionId;
	private String question;
	private InputType inputType;
	private List<CheckItemSvcDto> checkList;

	public FormDetailSvcDto(Question question) {
		this.questionId = question.getId();
		this.question = question.getQuestion();
		this.inputType = question.getInputType();
		this.checkList = question.getCheckLists().stream().map(CheckItemSvcDto::new).collect(Collectors.toList());
	}
}
