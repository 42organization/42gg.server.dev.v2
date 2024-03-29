package gg.party.api.user.room.controller.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class RoomListResDto {
	private List<RoomResDto> roomList;

	public RoomListResDto(List<RoomResDto> roomList) {
		this.roomList = roomList;
	}
}
