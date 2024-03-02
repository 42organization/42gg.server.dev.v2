package gg.party.api.user.room.controller.request;

import java.time.LocalDateTime;

import gg.data.party.Category;
import gg.data.party.Room;
import gg.data.party.type.RoomType;
import gg.data.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor
public class RoomCreateReqDto {
	private String title;
	private String content;
	private Long categoryId;
	private Integer maxPeople;
	private Integer minPeople;
	private LocalDateTime dueDate;

	public static Room toEntity(RoomCreateReqDto dto, User user, Category category) {
		return Room.builder()
			.host(user)
			.creator(user)
			.category(category)
			.title(dto.getTitle())
			.content(dto.getContent())
			.maxPeople(dto.getMaxPeople())
			.minPeople(dto.getMinPeople())
			.dueDate(dto.getDueDate())
			.status(RoomType.OPEN)
			.build();
	}
}
