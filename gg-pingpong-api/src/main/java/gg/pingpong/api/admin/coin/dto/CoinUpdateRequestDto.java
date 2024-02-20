package gg.pingpong.api.admin.coin.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CoinUpdateRequestDto {
	@NotNull(message = "intraId는 null이 될 수 없습니다.")
	private String intraId;
	@NotNull(message = "change는 null이 될 수 없습니다.")
	private int change;
	@NotNull(message = "content는 null이 될 수 없습니다.")
	private String content;
}
