package com.gg.server.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLiveResponseDto {
    private int notiCount;
    private String event;
    private String currentMatchMode;
}
