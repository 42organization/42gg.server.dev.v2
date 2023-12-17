package com.gg.server.domain.tournament.type;

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
}
