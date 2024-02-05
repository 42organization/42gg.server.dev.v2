package com.gg.server.data.manage.type;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FeedbackType {
	BUG("bug"),
	GAMERESULT("gameresult"),
	COMPLAINT("complaint"),
	CHEERS("cheers"),
	OPINION("opinion"),
	ETC("etc");

	private final String code;

	@JsonCreator
	public static FeedbackType getEnumFromValue(String value) {
		for (FeedbackType e : values()) {
			if (e.code.equals(value)) {
				return e;
			} else if (e.code.toUpperCase(Locale.ROOT).equals(value.toUpperCase(Locale.ROOT))) {
				return e;
			}
		}
		return null;
	}
}
