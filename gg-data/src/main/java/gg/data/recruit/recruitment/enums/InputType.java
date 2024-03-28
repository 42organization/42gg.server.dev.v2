package gg.data.recruit.recruitment.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum InputType {

	TEXT("text", "주관식"),
	SINGLE_CHECK("single check", "단일 선택지"),
	MULTI_CHECK("multi check", "복수 선택지");
	private final String type;
	private final String desc;
}
