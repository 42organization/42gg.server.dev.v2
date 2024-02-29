package gg.data.recruit.recruitment.type;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum InputType {

	TEXT("text", "텍스트"),
	CHECK_LIST("checkList", "체크리스트");
	private final String type;
	private final String desc;
}
