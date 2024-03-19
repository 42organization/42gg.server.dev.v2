package gg.recruit.api.user.service.param;

import java.util.List;

import gg.data.recruit.recruitment.enums.InputType;
import lombok.Getter;

@Getter
public class RecruitApplyFormParam {
	private Long questionId;
	private InputType inputType;
	private List<Long> checkedList;
	private String answer;

	public RecruitApplyFormParam(Long questionId, InputType inputType, List<Long> checkedList, String answer) {
		this.questionId = questionId;
		this.inputType = inputType;
		this.checkedList = checkedList;
		this.answer = answer;
	}
}
