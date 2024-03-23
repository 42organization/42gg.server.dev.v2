package gg.party.api.user.room.controller.request;

import java.time.LocalDateTime;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

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
	@Size(min = 1, max = 15)
	private String title;
	@Size(min = 1, max = 100)
	private String content;
	private Long categoryId;
	@Min(2)
	@Max(8)
	private Integer maxPeople;
	@Min(1)
	@Max(8)
	private Integer minPeople;
	@Min(1)
	@Max(13 * 60)
	private Integer minutesUntilDueDate;

	public static Room toEntity(RoomCreateReqDto dto, User user, Category category) {
		return Room.builder()
			.host(user)
			.creator(user)
			.category(category)
			.title(dto.getTitle())
			.content(dto.getContent())
			.currentPeople(1)
			.maxPeople(dto.getMaxPeople())
			.minPeople(dto.getMinPeople())
			.dueDate(LocalDateTime.now().plusMinutes(dto.getMinutesUntilDueDate()))
			.status(RoomType.OPEN)
			.build();
	}
}
