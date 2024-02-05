package com.gg.server.domain.team.dto;

import com.gg.server.data.user.type.SnsType;

public interface GameUser {
	Long getGameId();

	Long getUserId();

	String getIntraId();

	String getEmail();

	SnsType getSnsNotiOpt();
}
