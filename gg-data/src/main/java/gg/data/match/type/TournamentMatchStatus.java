package gg.data.match.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TournamentMatchStatus {
	// 토너먼트 매치 상태
	UNNECESSARY(0, "매칭 불필요함"),
	REQUIRED(1, "매칭 필요함"),
	ALREADY_MATCHED(2, "이미 매칭됨"),
	NO_MORE_MATCHES(3, "마지막 경기로 더이상 매칭할 게임이 없음");

	private final Integer value;
	private final String code;
}
