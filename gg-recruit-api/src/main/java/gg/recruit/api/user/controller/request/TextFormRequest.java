package gg.recruit.api.user.controller.request;

import gg.recruit.api.user.service.param.FormParam;
import gg.recruit.api.user.service.param.TextFormParam;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class TextFormRequest extends FormRequest{
	private String answer;

	@Override
	public FormParam toFormParam() {
		return new TextFormParam(answer, questionId, inputType);
	}
}
