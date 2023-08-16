package com.gg.server.domain.receipt.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemStatus {
    BEFORE("before", "사용 전"),
    WAITING("waiting", "사용 대기"),
    USING("using", "사용 중"),
    USED("used", "사용 완료"),
    DELETED("deleted", "삭제");

    private final String code;
    private final String desc;
}