package gg.party.api.user.room.controller.response;

import java.util.List;

import gg.data.party.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class RoomDetailResDto {
	private Room room;
	private String myNickname;
	private String hostNickname;
	private List<UserRoomResDto> roomUsers;
	private List<CommentResDto> comments;

	public RoomDetailResDto(Room room, String myNickname, String hostNickname, List<UserRoomResDto> roomUsers, List<CommentResDto> comments) {
		this.room = room;
		this.myNickname = myNickname;
		this.hostNickname = hostNickname;
		this.roomUsers = roomUsers;
		this.comments = comments;
	}
}
