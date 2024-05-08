package gg.recruit.api.user.controller.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import gg.data.recruit.recruitment.enums.InputType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RecruitApplyFormReqDto {
	@NotNull(message = "질문 ID를 입력해주세요.")
	private Long questionId;
	@NotNull(message = "입력 타입을 입력해주세요.")
	private InputType inputType;
	@NotNull(message = "체크된 항목을 입력해주세요.")
	private List<Long> checkedList;
	@NotBlank(message = "답변을 입력해주세요.")
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
