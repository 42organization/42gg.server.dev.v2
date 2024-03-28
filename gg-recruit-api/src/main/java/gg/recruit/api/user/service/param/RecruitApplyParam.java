package gg.recruit.api.user.service.param;

import java.util.List;

import lombok.Getter;

@Getter
public class RecruitApplyParam {
	private Long recruitId;
	private Long userId;
	private List<RecruitApplyFormParam> forms;

	public RecruitApplyParam(Long recruitId, Long userId, List<RecruitApplyFormParam> forms) {
		this.recruitId = recruitId;
		this.userId = userId;
		this.forms = forms;
	}

}
