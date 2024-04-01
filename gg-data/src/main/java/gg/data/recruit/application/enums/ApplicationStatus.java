package gg.data.recruit.application.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ApplicationStatus {

	PROGRESS_DOCS("progress", "서류 진행중", false),
	PROGRESS_INTERVIEW("progress", "면접 진행중", false),
	PASS("pass", "합격", true),
	FAIL("fail", "불합격", true);

	private final String status;
	private final String desc;
	public final boolean isFinal;
}
