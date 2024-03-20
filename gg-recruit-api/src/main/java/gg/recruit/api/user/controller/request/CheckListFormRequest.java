package gg.recruit.api.user.controller.request;

import java.util.List;

import gg.recruit.api.user.service.param.CheckListFormParam;
import gg.recruit.api.user.service.param.FormParam;
import lombok.Setter;

@Setter
public class CheckListFormRequest extends FormRequest {
	private List<Long> checkedList;

	@Override
	public FormParam toFormParam() {
		return new CheckListFormParam(checkedList, questionId, inputType);
	}
}
