package gg.data.pingpong.tournament.type;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TournamentStatus {
	BEFORE("before", "토너먼트 시작 전"),
	LIVE("live", "토너먼트 진행 중"),
	END("end", "토너먼트 종료");

	private final String code;
	private final String desc;

	@JsonCreator
	public static TournamentStatus getEnumFromValue(String value) {
		if (value == null) {
			return null;
		}
		for (TournamentStatus e : values()) {
			if (e.name().equals(value)) {
				return e;
			} else if (e.code.toUpperCase(Locale.ROOT).equals(value.toUpperCase(Locale.ROOT))) {
				return e;
			}
		}
		return null;
	}
}
