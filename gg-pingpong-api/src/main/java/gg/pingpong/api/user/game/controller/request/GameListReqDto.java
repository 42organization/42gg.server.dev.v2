package gg.pingpong.api.user.game.controller.request;

import gg.data.game.type.StatusType;
import lombok.Getter;

@Getter
public class GameListReqDto extends NormalGameListReqDto {
	private StatusType status;

	public GameListReqDto(Integer pageNum, Integer pageSize, String nickname, StatusType status) {
		super(pageNum, pageSize, nickname);
		this.status = status;
	}
}
