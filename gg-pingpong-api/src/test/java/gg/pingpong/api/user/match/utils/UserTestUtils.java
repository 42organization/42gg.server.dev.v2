package gg.pingpong.api.user.match.utils;

import java.util.UUID;

import gg.data.user.User;
import gg.data.user.type.RacketType;
import gg.data.user.type.RoleType;
import gg.data.user.type.SnsType;

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
