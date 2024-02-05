package com.gg.server.data.match;

import java.io.Serializable;

import javax.persistence.Id;

import org.springframework.data.redis.core.RedisHash;

import com.gg.server.data.match.type.Option;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@RedisHash("matchUser")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RedisMatchUser implements Serializable {
	@Id
	private Long userId;
	private Integer ppp;
	private Option option;

	public RedisMatchUser(Long userId, Integer ppp, Option option) {
		this.userId = userId;
		this.ppp = ppp;
		this.option = option;
	}
}
