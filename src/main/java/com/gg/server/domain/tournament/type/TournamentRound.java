package com.gg.server.domain.tournament.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Locale;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TournamentRound {
    // the final -> 결승
    // semi final  -> 4강
    // quarter final -> 8강
    // ordinal()로 sorting 사용되고 있으므로 순서 중요 -> 이후에 리팩토링으로 해결하겠습니다.
    THE_FINAL("1", null),
    SEMI_FINAL_1("4-1", THE_FINAL),
    SEMI_FINAL_2("4-2", THE_FINAL),
    QUARTER_FINAL_1("8-1", SEMI_FINAL_1),
    QUARTER_FINAL_2("8-2", SEMI_FINAL_1),
    QUARTER_FINAL_3("8-3", SEMI_FINAL_2),
    QUARTER_FINAL_4("8-4", SEMI_FINAL_2);

    private final String round;
    private final TournamentRound nextRound;

    @JsonCreator
    public static TournamentRound getEnumFromValue(String round) {
        for (TournamentRound e : values()) {
            if (e.name().equals(round)) {
                return e;
            } else if (e.round.toUpperCase(Locale.ROOT).equals(round.toUpperCase(Locale.ROOT))) {
                return e;
            }
        }
        return null;
    }

}
