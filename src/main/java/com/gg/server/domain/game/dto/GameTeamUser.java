package com.gg.server.domain.game.dto;

import com.gg.server.domain.game.type.StatusType;

import java.time.LocalDateTime;

public interface GameTeamUser {
    Long getGameId();

    LocalDateTime getStartTime();

    StatusType getStatus();
    String getT1IntraId();
    String getT1Image();
    Integer getT1Exp();
    String getT2IntraId();
    String getT2Image();
    Integer getT2Exp();
}
