package gg.party.api.admin.room.controller.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PageReqDto {
	private int page;
	private int size;
}
