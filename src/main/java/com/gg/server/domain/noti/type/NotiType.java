package com.gg.server.domain.noti.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Locale;

@Getter
@RequiredArgsConstructor
public enum NotiType {
    MATCHED("matched", "매칭이 성사되었퐁."),
    CANCELEDBYMAN("canceledbyman", "매칭이 취소되었퐁."),
    CANCELEDBYTIME("canceledbytime", "매칭이 상대 없음으로 취소되었퐁."),
    IMMINENT("imminent", "매치가 곧 시작될퐁."),
    ANNOUNCE("announce", "공지사항이 도착했퐁."),
    GIFT("gift", "새로운 선물이 도착했퐁.");

    private final String code;
    private final String message;

    public static NotiType of(String code) {
        return Arrays.stream(NotiType.values())
                .filter(notiType-> notiType.getCode().equals(code))
                .findAny()
                .orElse(ANNOUNCE);
    }

    @JsonCreator
    public static NotiType getEnumFromValue(String value) {
        for(NotiType e : values()) {
            if(e.code.equals(value)) {
                return e;
            }
            else if (e.code.toUpperCase(Locale.ROOT).equals(value.toUpperCase(Locale.ROOT))) {
                return e;
            }
        }
        return null;
    }

}
