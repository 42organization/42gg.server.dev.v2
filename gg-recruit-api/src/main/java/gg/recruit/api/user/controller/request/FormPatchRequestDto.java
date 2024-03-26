package gg.recruit.api.user.controller.request;

import java.util.List;
import java.util.stream.Collectors;

import gg.recruit.api.user.service.param.FormParam;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FormPatchRequestDto {
	List<FormRequest> forms;

	public List<FormParam> toFormParamList() {
		return forms.stream().map(FormRequest::toFormParam)
			.collect(Collectors.toList());
	}
}
