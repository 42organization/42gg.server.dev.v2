package gg.party.api.user.room.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class RoomCreateResDto {
	private Long roomId;

	public RoomCreateResDto(Long roomId) {
		this.roomId = roomId;
	}
}
