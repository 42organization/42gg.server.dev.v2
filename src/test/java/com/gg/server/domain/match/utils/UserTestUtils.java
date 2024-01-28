package com.gg.server.domain.match.utils;

import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;

import java.util.UUID;

public class UserTestUtils {
	public static User createUser() {
		String randomId = UUID.randomUUID().toString().substring(0, 30);
		return User.builder()
			.eMail("email")
			.intraId(randomId)
			.racketType(RacketType.PENHOLDER)
			.snsNotiOpt(SnsType.NONE)
			.roleType(RoleType.USER)
			.totalExp(1000)
			.build();
	}

	public static User createGuestUser() {
		String randomId = UUID.randomUUID().toString().substring(0, 30);
		return User.builder()
			.eMail("email")
			.intraId(randomId)
			.racketType(RacketType.PENHOLDER)
			.snsNotiOpt(SnsType.NONE)
			.roleType(RoleType.GUEST)
			.totalExp(1000)
			.build();
	}


}
