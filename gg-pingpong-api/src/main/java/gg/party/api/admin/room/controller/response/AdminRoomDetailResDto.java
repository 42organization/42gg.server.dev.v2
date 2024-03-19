package gg.party.api.admin.room.controller.response;

import java.time.LocalDateTime;
import java.util.List;

import gg.data.party.Room;
import gg.data.party.type.RoomType;
import gg.party.api.user.room.controller.response.UserRoomResDto;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class AdminRoomDetailResDto {
	private Long roomId;
	private String title;
	private Long categoryId;
	private Integer currentPeople;
	private Integer minPeople;
	private Integer maxPeople;
	private RoomType status;
	private LocalDateTime dueDate;
	private LocalDateTime createDate;
	private String myNickname;
	private String hostNickname;
	private List<UserRoomResDto> roomUsers;
	private List<AdminCommentResDto> comments;

	public AdminRoomDetailResDto(Room room, String hostNickname,
		List<UserRoomResDto> roomUsers, List<AdminCommentResDto> comments) {
		this.roomId = room.getId();
		this.title = room.getTitle();
		this.categoryId = room.getCategory().getId();
		this.currentPeople = room.getCurrentPeople();
		this.minPeople = room.getMinPeople();
		this.maxPeople = room.getMaxPeople();
		this.status = room.getStatus();
		this.dueDate = room.getDueDate();
		this.createDate = room.getCreatedAt();
		this.myNickname = null;
		this.hostNickname = hostNickname;
		this.roomUsers = roomUsers;
		this.comments = comments;
	}
}
