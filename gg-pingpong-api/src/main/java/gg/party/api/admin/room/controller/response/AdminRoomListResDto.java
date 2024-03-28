package gg.party.api.admin.room.controller.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class AdminRoomListResDto {
	private List<AdminRoomResDto> adminRoomList;
	private int totalPages;

	public AdminRoomListResDto(List<AdminRoomResDto> adminRoomList, int totalPages) {
		this.adminRoomList = adminRoomList;
		this.totalPages = totalPages;
	}
}
