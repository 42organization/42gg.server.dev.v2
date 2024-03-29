package gg.party.api.admin.room.controller.response;

import java.time.LocalDateTime;

import gg.data.party.Room;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminRoomResDto {
	private Long roomId;
	private String hostIntraId;
	private String creatorIntraId;
	private String categoryName;
	private String title;
	private String content;
	private Integer currentPeople;
	private Integer maxPeople;
	private Integer minPeople;
	private LocalDateTime dueDate;
	private LocalDateTime createDate;
	private LocalDateTime startDate;
	private String status;

	public AdminRoomResDto(Room room) {
		this.roomId = room.getId();
		this.hostIntraId = room.getHost().getIntraId();
		this.creatorIntraId = room.getCreator().getIntraId();
		this.categoryName = room.getCategory().getName();
		this.title = room.getTitle();
		this.content = room.getContent();
		this.currentPeople = room.getCurrentPeople();
		this.maxPeople = room.getMaxPeople();
		this.minPeople = room.getMinPeople();
		this.dueDate = room.getDueDate();
		this.createDate = room.getCreatedAt();
		this.startDate = room.getStartDate();
		this.status = room.getStatus().toString();
	}
}
