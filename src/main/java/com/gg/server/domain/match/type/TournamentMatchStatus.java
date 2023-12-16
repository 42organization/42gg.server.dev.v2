package com.gg.server.domain.match.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.gg.server.domain.match.exception.OptionInvalidException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@Getter
@RequiredArgsConstructor
public enum TournamentMatchStatus {
    // 토너먼트 매치 상태
    IMPOSSIBLE(0, "매칭 불가능"),
    POSSIBLE(1, "매칭 가능"),
    ALREADY_MATCHED(2, "이미 매칭됨");

    private final Integer value;
    private final String code;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static TournamentMatchStatus getEnumValue(String code) {
        for(TournamentMatchStatus e : values()) {
            if(e.code.equals(code)) {
                return e;
            }
            else if (e.code.toUpperCase(Locale.ROOT).equals(code.toUpperCase(Locale.ROOT)))
                return e;
        }
        throw new OptionInvalidException();
    }

}
