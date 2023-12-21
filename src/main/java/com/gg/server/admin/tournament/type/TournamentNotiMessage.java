package com.gg.server.admin.tournament.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TournamentNotiMessage {
    TOURNAMENT_STARTED("참가 신청한 토너먼트 개최 당일입니다. 개최 시간을 확인하시고 늦지 않게 참석하시기 바랍니다!"),
    TOURNAMENT_CANCELED("참가 신청한 토너먼트가 신청 인원 미달로 취소되었습니다."),
    GAME_MATCHED("토너먼트 게임이 매칭되었습니다! 경기 상대를 확인해주세요."),
    GAME_CANCELED("토너먼트 게임이 취소되었습니다.");

    private final String message;
}
