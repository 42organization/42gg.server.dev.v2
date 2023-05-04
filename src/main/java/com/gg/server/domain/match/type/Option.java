package com.gg.server.domain.match.type;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Locale;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Option {
    //match에서는 Both를 써서 자료형 따로 만듬
    BOTH(0,"both"),
    NORMAL(1,"normal"),
    RANK(2,"rank");

    // 모드는 3가지가 있음.

    private final Integer value;
    private final String code;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static Option getEnumValue(String code) {
        for(Option e : values()) {
            if(e.code.equals(code)) {
                return e;
            }
            else if (e.code.toUpperCase(Locale.ROOT).equals(code.toUpperCase(Locale.ROOT)))
                return e;
        }
        return null;
    }

}
