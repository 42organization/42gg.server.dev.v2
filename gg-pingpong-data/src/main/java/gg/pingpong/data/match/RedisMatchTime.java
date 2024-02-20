package gg.pingpong.data.match;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Id;

import org.springframework.data.redis.core.RedisHash;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import gg.pingpong.data.match.type.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@RedisHash("matchTime")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RedisMatchTime implements Serializable {
	@Id
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime startTime;
	private Option option;

	public RedisMatchTime(LocalDateTime startTime, Option option) {
		this.startTime = startTime;
		this.option = option;
	}
}
