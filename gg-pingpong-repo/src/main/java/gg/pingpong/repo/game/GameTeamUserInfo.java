package gg.pingpong.repo.game;

import java.time.LocalDateTime;

import gg.pingpong.data.game.type.Mode;
import gg.pingpong.data.game.type.StatusType;

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
