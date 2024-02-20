package gg.pingpong.data.store.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HistoryType {

	ATTENDANCECOIN("출석"),
	NORMAL("일반전 참가"),
	RANKWIN("랭크전 승리"),
	RANKLOSE("랭크전 패배");

	private final String history;
}
