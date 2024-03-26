package gg.data.pingpong.match.type;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SlotStatus {
	OPEN("open"),
	CLOSE("close"),
	MYTABLE("mytable"),
	MATCH("match");
	private final String code;

	@JsonCreator
	public static SlotStatus getEnumFromValue(String value) {
		for (SlotStatus e : values()) {
			if (e.code.equals(value)) {
				return e;
			} else if (e.code.toUpperCase(Locale.ROOT).equals(value.toUpperCase(Locale.ROOT))) {
				return e;
			}
		}
		return null;
	}
}
