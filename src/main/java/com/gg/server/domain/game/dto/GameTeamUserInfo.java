package com.gg.server.domain.game.dto;

import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;

import java.time.LocalDateTime;

public interface GameTeamUserInfo {
    Long getGameId();
    LocalDateTime getStartTime();
    StatusType getStatus();
    Mode getMode();
    Long getTeamId();
    Integer getScore();
    Long getUserId();
    String getIntraId();
    Integer getExp();
    String getImage();

}
