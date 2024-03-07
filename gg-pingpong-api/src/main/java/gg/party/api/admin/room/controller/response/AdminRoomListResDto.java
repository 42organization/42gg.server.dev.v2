package gg.party.api.admin.room.controller.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class AdminRoomListResDto {
	private List<AdminRoomResDto> roomList;

	public AdminRoomListResDto(List<AdminRoomResDto> roomList) {
		this.roomList = roomList;
	}
}
