package com.gg.server.domain.team.dto;

import com.gg.server.domain.user.type.SnsType;

public interface GameUser {
	Long getGameId();

	Long getUserId();

	String getIntraId();

	String getEmail();

	SnsType getSnsNotiOpt();
}
