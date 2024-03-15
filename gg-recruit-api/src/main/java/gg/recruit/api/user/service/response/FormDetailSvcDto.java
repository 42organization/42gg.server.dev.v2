package gg.recruit.api.user.service.response;

import java.util.List;

import gg.data.recruit.recruitment.enums.InputType;
import lombok.Getter;

@Getter
public class FormDetailSvcDto {
	private Long questionId;
	private String question;
	private InputType inputType;
	private List<CheckItemSvcDto> checkList;

	public FormDetailSvcDto(Long questionId) {
		this.questionId = questionId;
	}
}
