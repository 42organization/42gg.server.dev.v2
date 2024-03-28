package gg.recruit.api.user.controller.response;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum RecruitmentStatus {
	BEFORE("모집전"), PROGRESS("모집중"), FINISHED("모집완료");
	private final String desc;
}
