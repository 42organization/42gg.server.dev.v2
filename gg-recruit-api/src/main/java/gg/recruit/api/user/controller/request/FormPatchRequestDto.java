package gg.recruit.api.user.controller.request;

import java.util.List;

import gg.recruit.api.user.service.param.FormParam;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class FormPatchRequestDto {
	List<FormRequest> forms;

	public List<FormParam> toFormParamList() {
		return forms.stream().map(FormRequest::toFormParam).toList();
	}
}
