package com.gg.server.domain.receipt.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemStatus {
    BEFORE("before", "사용 전"), USING("using", "사용 중"),
    USED("used", "사용 완료");

    private final String code;
    private final String desc;
}