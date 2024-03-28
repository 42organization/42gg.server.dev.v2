package gg.recruit.api.user.controller.response;

import java.util.List;
import java.util.stream.Collectors;

import gg.data.recruit.recruitment.enums.InputType;
import gg.recruit.api.user.service.response.FormDetailSvcDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FormDetailResDto {
	private Long questionId;
	private String question;
	private InputType inputType;
	private List<CheckItemResDto> checkList;

	public FormDetailResDto(FormDetailSvcDto dto) {
		this.questionId = dto.getQuestionId();
		this.question = dto.getQuestion();
		this.inputType = dto.getInputType();
		this.checkList = dto.getCheckList().stream().map(CheckItemResDto::new).collect(Collectors.toList());
	}
}
