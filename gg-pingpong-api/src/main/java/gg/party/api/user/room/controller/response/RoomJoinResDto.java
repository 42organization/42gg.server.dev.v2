package gg.party.api.user.room.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class RoomJoinResDto {
	private Long roomId;

	public RoomJoinResDto(Long roomId) {
		this.roomId = roomId;
	}
}
