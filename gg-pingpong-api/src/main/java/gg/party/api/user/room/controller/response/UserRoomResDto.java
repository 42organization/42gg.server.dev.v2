package gg.party.api.user.room.controller.response;

import gg.data.party.UserRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class UserRoomResDto {
	private Long roomUserId;
	private String nickname;
	private String intraId;

	public UserRoomResDto(UserRoom userRoom) {
		this.roomUserId = userRoom.getId();
		this.nickname = userRoom.getNickname();
		this.intraId = null;
	}

	public UserRoomResDto(UserRoom userRoom, String intraId) {
		this.roomUserId = userRoom.getId();
		this.nickname = userRoom.getNickname();
		this.intraId = intraId;
	}
}
