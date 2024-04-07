package gg.recruit.api.admin.controller.response;

import java.util.List;
import java.util.stream.Collectors;

import gg.data.recruit.recruitment.enums.InputType;
import gg.recruit.api.admin.service.result.FormDetailAdminSvcDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class FormDetailAdminResDto {
	private Long questionId;
	private String question;
	private InputType inputType;
	private List<CheckItemAdminResDto> checkList;

	public FormDetailAdminResDto(FormDetailAdminSvcDto dto) {
		this.questionId = dto.getQuestionId();
		this.question = dto.getQuestion();
		this.inputType = dto.getInputType();
		this.checkList = dto.getCheckList().stream()
			.map(CheckItemAdminResDto::new).collect(Collectors.toList());
	}
}
