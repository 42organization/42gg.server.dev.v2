package gg.data.party.type;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomType {
	OPEN("open", "방 시작 전"),
	START("live", "방 진행 중"),
	FINISH("end", "방 종료"),
	HIDDEN("end", "방 종료 후 가림"),
	FAIL("end", "매칭 실패한 방");

	private final String code;
	private final String desc;

	@JsonCreator
	public static RoomType getEnumFromValue(String value) {
		if (value == null) {
			return null;
		}
		for (RoomType e : values()) {
			if (e.name().equals(value)) {
				return e;
			} else if (e.code.toUpperCase(Locale.ROOT).equals(value.toUpperCase(Locale.ROOT))) {
				return e;
			}
		}
		return null;
	}
}
