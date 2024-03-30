package gg.party.api.user.room.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class RoomStartResDto {
	private Long roomId;

	public RoomStartResDto(Long roomId) {
		this.roomId = roomId;
	}
}
