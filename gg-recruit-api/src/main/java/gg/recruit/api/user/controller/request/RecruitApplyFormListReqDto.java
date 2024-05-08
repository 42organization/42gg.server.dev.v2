package gg.recruit.api.user.controller.request;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecruitApplyFormListReqDto {
	@NotNull(message = "forms는 null일 수 없습니다.")
	@Valid
	private List<RecruitApplyFormReqDto> forms;

	public RecruitApplyFormListReqDto(List<RecruitApplyFormReqDto> forms) {
		this.forms = forms;
	}
}
