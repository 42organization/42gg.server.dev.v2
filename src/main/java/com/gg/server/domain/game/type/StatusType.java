package com.gg.server.domain.game.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Locale;

@Getter
@RequiredArgsConstructor
public enum StatusType {

    BEFORE("before"), // 게임 시작 전
    LIVE("live"), // 게임 진행 중
    WAIT("wait"), // 게임 끝나고 점수입력 기다리는 상태
    END("end"); // 게임이 끝나고 점수입력도 완료

    private final String code;

    @JsonCreator
    public static StatusType getEnumFromValue(String value) {
        for(StatusType e : values()) {
            if(e.name().equals(value)) {
                return e;
            }
            else if (e.code.toUpperCase(Locale.ROOT).equals(value.toUpperCase(Locale.ROOT))) {
                return e;
            }
        }
        return null;
    }
}
