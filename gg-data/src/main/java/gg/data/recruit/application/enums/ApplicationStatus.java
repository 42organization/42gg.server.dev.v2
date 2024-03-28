package gg.data.recruit.application.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ApplicationStatus {

	PROGRESS_DOCS("progress", "서류 진행중"),
	PROGRESS_INTERVIEW("progress", "면접 진행중"),
	PASS("pass", "합격"),
	FAIL("fail", "불합격");

	private final String status;
	private final String desc;
}
