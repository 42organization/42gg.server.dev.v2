package gg.data.recruit.recruitment.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum InputType {

	TEXT("text", "텍스트"),
	SINGLE_CHECK("single_check", "싱글 체크리스트"),
	MULTI_CHECK("multi_check", "멀티 체크리스트");
	private final String type;
	private final String desc;
}
