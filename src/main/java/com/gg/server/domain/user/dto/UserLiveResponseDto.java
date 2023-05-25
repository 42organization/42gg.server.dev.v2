package com.gg.server.domain.user.dto;

import com.gg.server.domain.game.type.Mode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLiveResponseDto {
    private int notiCount;
    private String event;
    private Mode currentMatchMode;
    private Long gameId;
}
