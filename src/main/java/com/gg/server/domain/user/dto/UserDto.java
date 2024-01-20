package com.gg.server.domain.user.dto;

import com.gg.server.domain.user.data.User;
import com.gg.server.domain.user.type.RacketType;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.domain.user.type.SnsType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDto {
	private Long id;
	private String intraId;
	private String eMail;
	private RacketType racketType;
	private RoleType roleType;
	private Integer totalExp;
	private SnsType snsNotiOpt;
	private Long kakaoId;
	private String textColor;
	private Integer ggCoin;

	public static UserDto from(User user) {
		UserDto userDto;
		if (user == null) {
			userDto = null;
		} else {
			userDto = UserDto.builder()
				.id(user.getId())
				.intraId(user.getIntraId())
				.eMail(user.getEMail())
				.racketType(user.getRacketType())
				.roleType(user.getRoleType())
				.totalExp(user.getTotalExp())
				.snsNotiOpt(user.getSnsNotiOpt())
				.kakaoId(user.getKakaoId())
				.ggCoin(user.getGgCoin())
				.build();
		}
		return userDto;
	}

	@Override
	public String toString() {
		return "UserDto{"
			+ "id=" + id
			+ ", intraId='" + intraId + '\''
			+ ", eMail='" + eMail + '\''
			+ ", racketType=" + racketType
			+ ", roleType=" + roleType
			+ ", totalExp=" + totalExp
			+ ", snsNotiOpt=" + snsNotiOpt
			+ ", kakaoId=" + kakaoId
			+ '}';
	}
}
