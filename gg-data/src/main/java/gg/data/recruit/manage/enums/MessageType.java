package gg.data.recruit.manage.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MessageType {

	INTERVIEW("interview", "면접 안내"),
	PASS("pass", "합격 안내"),
	FAIL("fail", "불합격 안내");

	private final String messageType;
	private final String desc;
}
