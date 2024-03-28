package gg.recruit.api.user.controller.request;

import java.util.List;

import gg.data.recruit.recruitment.enums.InputType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecruitApplyFormReqDto {
	private Long questionId;
	private InputType inputType;
	private List<Long> checkedList;
	private String answer;

	public RecruitApplyFormReqDto(Long questionId, String answer) {
		this.questionId = questionId;
		this.inputType = InputType.TEXT;
		this.answer = answer;
	}

	public RecruitApplyFormReqDto(Long questionId, List<Long> checkedList) {
		this.questionId = questionId;
		this.inputType = InputType.MULTI_CHECK;
		this.checkedList = checkedList;
	}

	public RecruitApplyFormReqDto(Long questionId, Long checkedList) {
		this.questionId = questionId;
		this.inputType = InputType.SINGLE_CHECK;
		this.checkedList = List.of(checkedList);
	}
}
