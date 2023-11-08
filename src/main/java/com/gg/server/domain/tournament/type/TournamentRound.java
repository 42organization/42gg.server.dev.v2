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
    THE_FINAL("1"),
    SEMI_FINAL_1("4-1"),
    SEMI_FINAL_2("4-2"),
    QUARTER_FINAL_1("8-1"),
    QUARTER_FINAL_2("8-2"),
    QUARTER_FINAL_3("8-3"),
    QUARTER_FINAL_4("8-4");

    private final String round;

    @JsonCreator
    public static TournamentRound getEnumFromValue(String value) {
        for (TournamentRound e : values()) {
            if (e.name().equals(value)) {
                return e;
            } else if (e.round.toUpperCase(Locale.ROOT).equals(value.toUpperCase(Locale.ROOT))) {
                return e;
            }
        }
        return null;
    }

}
