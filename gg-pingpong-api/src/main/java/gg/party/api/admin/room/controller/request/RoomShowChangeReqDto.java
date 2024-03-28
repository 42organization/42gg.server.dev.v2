package gg.party.api.admin.room.controller.request;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomShowChangeReqDto {
	@NotNull
	private String status;

	public RoomShowChangeReqDto(String status) {
		this.status = status;
	}
}
