package com.gg.server.domain.match.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TournamentMatchStatus {
	// 토너먼트 매치 상태
	IMPOSSIBLE(0, "매칭 불가능"),
	POSSIBLE(1, "매칭 가능"),
	ALREADY_MATCHED(2, "이미 매칭됨");

	private final Integer value;
	private final String code;
}
