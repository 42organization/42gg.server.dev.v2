package com.gg.server.domain.tournament.type;

import java.util.ArrayList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public enum TournamentRound {
    // the final -> 결승
    // semi final  -> 4강
    // quarter final -> 8강
    // ordinal()로 sorting 사용되고 있으므로 순서 중요 -> 이후에 리팩토링으로 해결하겠습니다.
    THE_FINAL("1", null, 2),
    SEMI_FINAL_1("4-1", THE_FINAL, 4),
    SEMI_FINAL_2("4-2", THE_FINAL, 4),
    QUARTER_FINAL_1("8-1", SEMI_FINAL_1, 8),
    QUARTER_FINAL_2("8-2", SEMI_FINAL_1, 8),
    QUARTER_FINAL_3("8-3", SEMI_FINAL_2, 8),
    QUARTER_FINAL_4("8-4", SEMI_FINAL_2, 8);

    private final String round;
    private final TournamentRound nextRound;
    private final int roundNumber;

    public static List<TournamentRound> getSameRounds(TournamentRound round) {
        List<TournamentRound> sameRounds = new ArrayList<>();
        for (TournamentRound e : values()) {
            if (e.roundNumber == round.roundNumber) {
                sameRounds.add(e);
            }
        }
        return sameRounds;
    }

    /**
     * 이전 TournamentRound의 roundNum를 반환한다.
     * @param round - 현재 라운드
     * @return 이전 라운드의 roundNum, 없을 경우 -1 반환
     */
    public static int getPreviousRoundNumber(TournamentRound round) {
        for (TournamentRound e : values()) {
            if (e.nextRound == round) {
                return e.roundNumber;
            }
        }
        return -1;
    }
}
