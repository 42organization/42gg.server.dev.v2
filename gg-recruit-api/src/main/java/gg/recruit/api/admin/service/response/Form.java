package gg.recruit.api.admin.service.response;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.Recruitment;
import gg.data.recruit.recruitment.enums.InputType;
import lombok.Getter;

@Getter
public class Form {
	@NotBlank(message = "질문을 입력해주세요.")
	@Size(min = 1, max = 300, message = "질문은 300자 이내로 입력해주세요.")
	String question;

	@NotBlank(message = "입력타입을 선택해주세요.")
	@Size(min = 1, max = 20, message = "입력타입은 20자 이내로 입력해주세요.")
	InputType inputType;

	@NotNull(message = "선택지를 입력해주세요.")
	List<@NotNull @NotEmpty @Size(min = 1, max = 100, message = "100자 이내로 입력해주세요.") String> checkList;

	public Question toQuestion(Recruitment recruitment, int sortNum) {
		return new Question(recruitment, inputType, question, sortNum);
	}
}
