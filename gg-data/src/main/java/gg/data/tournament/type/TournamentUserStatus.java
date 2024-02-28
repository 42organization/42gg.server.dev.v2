package gg.data.tournament.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TournamentUserStatus {
	PLAYER("player", "토너먼트 참가자"),
	WAIT("wait", "토너먼트 대기자"),
	BEFORE("before", "토너먼트 신청 전");

	private final String code;
	private final String desc;
}
