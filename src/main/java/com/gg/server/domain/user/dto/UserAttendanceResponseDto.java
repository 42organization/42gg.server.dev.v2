package com.gg.server.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAttendanceResponseDto {
    private int beforeCoin;
    private int afterCoin;
    private int coinIncrement;
}
