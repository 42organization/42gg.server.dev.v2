package gg.recruit.api.admin.service.response;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.Recruitment;
import gg.data.recruit.recruitment.enums.InputType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Form {
	@NotBlank(message = "질문을 입력해주세요.")
	@Size(min = 1, max = 300, message = "질문은 300자 이내로 입력해주세요.")
	String question;

	InputType inputType;

	List<@NotNull @NotEmpty @Size(min = 1, max = 100, message = "100자 이내로 입력해주세요.") String> checkList;

	@Builder
	public Form(String question, InputType inputType, List<String> checkList) {
		this.question = question;
		this.inputType = inputType;
		this.checkList = checkList;
	}

	public Question toQuestion(Recruitment recruitment, int sortNum) {
		return new Question(recruitment, inputType, question, sortNum);
	}
}
