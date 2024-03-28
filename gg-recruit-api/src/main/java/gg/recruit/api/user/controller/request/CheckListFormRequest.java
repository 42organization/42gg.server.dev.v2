package gg.recruit.api.user.controller.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import gg.data.recruit.recruitment.enums.InputType;
import gg.recruit.api.user.service.param.CheckListFormParam;
import gg.recruit.api.user.service.param.FormParam;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class CheckListFormRequest extends FormRequest {

	@JsonProperty("checkedList")
	private List<Long> checkedList;

	public CheckListFormRequest(Long questionId, InputType inputType, List<Long> checkedList) {
		this.checkedList = checkedList;
		this.questionId = questionId;
		this.inputType = inputType;
	}

	@Override
	public FormParam toFormParam() {
		return new CheckListFormParam(checkedList, questionId, inputType);
	}
}
