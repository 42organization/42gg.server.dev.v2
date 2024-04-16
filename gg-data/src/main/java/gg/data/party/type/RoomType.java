package gg.data.party.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomType {
	OPEN("open", "방 시작 전"),
	START("live", "방 진행 중"),
	FINISH("end", "방 종료"),
	HIDDEN("end", "신고로 인한 가림 상태"),
	FAIL("end", "매칭 실패한 방");

	private final String code;
	private final String desc;
}
