package gg.pingpong.api.user.user.dto;

import com.gg.server.data.game.type.Mode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserLiveResponseDto {
	private int notiCount;
	private String event;
	private Mode currentMatchMode;
	private Long gameId;
}
