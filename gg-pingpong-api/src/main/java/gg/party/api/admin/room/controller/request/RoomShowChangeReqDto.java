package gg.party.api.admin.room.controller.request;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoomShowChangeReqDto {
	@NotNull
	private String status;
}
