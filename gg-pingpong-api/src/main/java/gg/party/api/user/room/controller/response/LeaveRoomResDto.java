package gg.party.api.user.room.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class LeaveRoomResDto {
	private String nickname;

	public LeaveRoomResDto(String nickname) {
		this.nickname = nickname;
	}
}
