package gg.recruit.api.user.service.param;

import java.util.List;

import gg.recruit.api.user.controller.request.FormRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FormPatchParam {
	Long applicationId;
	Long recruitmentId;
	Long userId;
	List<FormParam> forms;

	public List<Long> getQuestionIds() {
		return forms.stream().map(FormParam::getQuestionId).toList();
	}

}
