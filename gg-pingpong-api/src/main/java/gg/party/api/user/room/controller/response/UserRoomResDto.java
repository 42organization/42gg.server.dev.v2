package gg.party.api.user.room.controller.response;

import gg.data.party.UserRoom;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class UserRoomResDto {
	private Long roomUserId;
	private String nickname;
	private String intraId;
	private String userImage;

	public UserRoomResDto(UserRoom userRoom) {
		this.roomUserId = userRoom.getId();
		this.nickname = userRoom.getNickname();
		this.intraId = null;
		this.userImage = null;
	}

	public UserRoomResDto(UserRoom userRoom, String intraId, String userImage) {
		this.roomUserId = userRoom.getId();
		this.nickname = userRoom.getNickname();
		this.intraId = intraId;
		this.userImage = userImage;
	}
}
