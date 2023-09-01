package com.gg.server.domain.receipt.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemStatus {
    BEFORE("사용 전"),
    WAITING("사용 대기"),
    USING("사용 중"),
    USED("사용 완료"),
    DELETED("삭제");

    private final String description;
}