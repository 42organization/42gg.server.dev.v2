package gg.party.api.user.room.controller.response;

import java.time.LocalDateTime;

import gg.data.party.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoomResDto {
	private Long roomId;
	private Long hostId;
	private Long creatorId;
	private Long categoryId;
	private String title;
	private String content;
	private Integer maxPeople;
	private Integer minPeople;
	private LocalDateTime dueDate;
	private LocalDateTime createDate;
	private String roomStatus;

	public RoomResDto(Room room) {
		this.roomId = room.getRoomId();
		this.hostId = room.getHost().getId();
		this.creatorId = room.getCreator().getId();
		this.categoryId = room.getCategory().getCategoryId();
		this.title = room.getTitle();
		this.content = room.getContent();
		this.maxPeople = room.getMaxPeople();
		this.minPeople = room.getMinPeople();
		this.dueDate = room.getDueDate();
		this.createDate = room.getCreateDate();
		this.roomStatus = room.getRoomStatus().toString();
	}
}
