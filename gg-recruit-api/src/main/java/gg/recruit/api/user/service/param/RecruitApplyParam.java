package gg.recruit.api.user.service.param;

import java.util.List;

import lombok.Getter;

@Getter
public class RecruitApplyParam {
	private Long userId;
	private Long recruitId;
	private List<RecruitApplyFormParam> forms;

	public RecruitApplyParam(Long userId, Long recruitId, List<RecruitApplyFormParam> forms) {
		this.userId = userId;
		this.recruitId = recruitId;
		this.forms = forms;
	}

}
