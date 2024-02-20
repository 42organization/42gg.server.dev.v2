package gg.pingpong.api.user.noti.dto;

import com.gg.server.data.user.type.SnsType;
import com.gg.server.domain.team.dto.GameUser;

import lombok.Getter;

@Getter
public class UserNotiDto {
	private Long userId;
	private Long gameId;
	private String intraId;
	private SnsType snsNotiOpt;
	private String email;

	public UserNotiDto(GameUser gameUser) {
		this.gameId = gameUser.getGameId();
		this.userId = gameUser.getUserId();
		this.intraId = gameUser.getIntraId();
		this.email = gameUser.getEmail();
		this.snsNotiOpt = gameUser.getSnsNotiOpt();
	}
}
