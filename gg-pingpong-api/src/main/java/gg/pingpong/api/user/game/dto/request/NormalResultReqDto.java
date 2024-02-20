package gg.pingpong.api.user.game.dto.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Getter;

@Getter
public class NormalResultReqDto {
	@NotNull(message = "gameId 는 필수 값입니다.")
	@Positive(message = "gameId 는 양수만 입력 가능합니다.")
	private Long gameId;

}
