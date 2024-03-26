package gg.recruit.api.user.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import gg.data.recruit.recruitment.enums.InputType;
import gg.recruit.api.user.service.param.FormParam;
import gg.recruit.api.user.service.param.TextFormParam;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class TextFormRequest extends FormRequest {

	@JsonProperty("answer")
	private String answer;

	public TextFormRequest(Long questionId, InputType inputType, String answer) {
		this.questionId = questionId;
		this.inputType = inputType;
		this.answer = answer;
	}

	@Override
	public FormParam toFormParam() {
		return new TextFormParam(answer, questionId, inputType);
	}
}
