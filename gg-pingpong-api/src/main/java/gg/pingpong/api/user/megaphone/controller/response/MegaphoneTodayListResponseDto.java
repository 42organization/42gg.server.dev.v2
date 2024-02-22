package gg.pingpong.api.user.megaphone.controller.response;

import gg.pingpong.data.store.redis.MegaphoneRedis;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MegaphoneTodayListResponseDto {
	private Long megaphoneId;
	private String content;
	private String intraId;

	public MegaphoneTodayListResponseDto(MegaphoneRedis megaphoneRedis) {
		this.megaphoneId = megaphoneRedis.getId();
		this.content = megaphoneRedis.getContent();
		this.intraId = megaphoneRedis.getIntraId();
	}
}
