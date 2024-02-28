package gg.pingpong.repo.game.out;

import java.time.LocalDateTime;

import gg.pingpong.data.game.type.Mode;
import gg.pingpong.data.game.type.StatusType;

public interface GameTeamUser {
	Long getGameId();

	LocalDateTime getStartTime();

	LocalDateTime getEndTime();

	StatusType getStatus();

	Mode getMode();

	Long getT1TeamId();

	Integer getT1Wins();

	Integer getT1Losses();

	String getT1IntraId();

	String getT1Image();

	Integer getT1Exp();

	Integer getT1Score();

	Boolean getT1IsWin();

	Long getT2TeamId();

	Integer getT2Wins();

	Integer getT2Losses();

	String getT2IntraId();

	String getT2Image();

	Integer getT2Exp();

	Integer getT2Score();

	Boolean getT2IsWin();
}
