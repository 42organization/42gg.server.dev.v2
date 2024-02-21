package gg.pingpong.api.user.user.dto;

import gg.pingpong.data.user.type.RacketType;
import gg.pingpong.data.user.type.SnsType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserModifyRequestDto {

	private RacketType racketType;
	private String statusMessage;
	private SnsType snsNotiOpt;
}
