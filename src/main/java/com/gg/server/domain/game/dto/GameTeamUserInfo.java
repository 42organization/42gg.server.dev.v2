package com.gg.server.domain.game.dto;

import java.time.LocalDateTime;

import com.gg.server.data.game.type.Mode;
import com.gg.server.data.game.type.StatusType;

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
