package gg.repo.game.out;

import gg.data.user.type.SnsType;

public interface GameUser {
	Long getGameId();

	Long getUserId();

	String getIntraId();

	String getEmail();

	SnsType getSnsNotiOpt();
}
