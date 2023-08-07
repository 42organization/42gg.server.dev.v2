package com.gg.server.domain.coin.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HistoryType {

    ATTENDANCECOIN("출석 입니다."),
    NORMAL("일반전 코인 획득"),
    RANKWIN("랭크전 승리 코인 획득"),
    RANKLOSE("랭크전 패배 코인 획득");

    private final String history;
}
