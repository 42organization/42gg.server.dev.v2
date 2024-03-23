package gg.recruit.api.user.controller.request;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecruitApplyFormListReqDto {
	private List<RecruitApplyFormReqDto> forms;

	public RecruitApplyFormListReqDto(List<RecruitApplyFormReqDto> forms) {
		this.forms = forms;
	}
}
