package gg.pingpong.api.user.game.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Getter;

@Getter
public class RankGameListReqDto extends NormalGameListReqDto {
	@Positive
	@NotNull(message = "seasonId 는 필수 값입니다.")
	private Long seasonId;

	public RankGameListReqDto(Integer pageNum, Integer pageSize, String nickname, Long seasonId) {
		super(pageNum, pageSize, nickname);
		this.seasonId = seasonId;
	}
}
