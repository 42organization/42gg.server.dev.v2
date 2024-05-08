package gg.recruit.api.admin.service.param;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.Recruitment;
import gg.data.recruit.recruitment.enums.InputType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class FormParam {
	@NotBlank(message = "질문을 입력해주세요.")
	@Size(min = 1, max = 300, message = "질문은 300자 이내로 입력해주세요.")
	String question;

	@NotNull(message = "inputType을 입력해주세요.")
	InputType inputType;

	@Valid
	List<CheckListContent> checkList;

	@Builder
	public FormParam(String question, InputType inputType, List<CheckListContent> checkList) {
		this.question = question;
		this.inputType = inputType;
		this.checkList = checkList;
	}

	public Question toQuestion(Recruitment recruitment, int sortNum) {
		return new Question(recruitment, inputType, question, sortNum);
	}
}
