package com.gg.server.domain.game.dto;

import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;

import java.time.LocalDateTime;

public interface GameTeamUser {
    Long getGameId();
    LocalDateTime getStartTime();
    StatusType getStatus();
    Mode getMode();
    Integer getT1Wins();
    Integer getT1Losses();
    String getT1IntraId();
    String getT1Image();
    Integer getT1Exp();
    Integer getT1Score();
    Boolean getT1IsWin();
    Integer getT2Wins();
    Integer getT2Losses();
    String getT2IntraId();
    String getT2Image();
    Integer getT2Exp();
    Integer getT2Score();
    Boolean getT2IsWin();
}
