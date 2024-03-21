package gg.party.api.user.room.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoomStartResDto {
	private Long roomId;

	public RoomStartResDto(Long roomId) {
		this.roomId = roomId;
	}
}
