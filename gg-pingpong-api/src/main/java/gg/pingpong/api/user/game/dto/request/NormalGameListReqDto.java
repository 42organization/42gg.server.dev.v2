package gg.pingpong.api.user.game.dto.request;

import com.gg.server.global.dto.PageRequestDto;

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
