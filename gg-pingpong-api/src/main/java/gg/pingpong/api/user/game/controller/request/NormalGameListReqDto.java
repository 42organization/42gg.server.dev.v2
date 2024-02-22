package gg.pingpong.api.user.game.controller.request;

import gg.pingpong.api.global.dto.PageRequestDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NormalGameListReqDto extends PageRequestDto {
	private String intraId;

	public NormalGameListReqDto(Integer page, Integer size, String intraId) {
		super(page, size);
		this.intraId = intraId;
	}
}
