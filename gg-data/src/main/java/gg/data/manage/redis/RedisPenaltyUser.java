package gg.data.manage.redis;

import java.time.LocalDateTime;

import javax.persistence.Id;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RedisPenaltyUser {
	@Id
	private String id;
	private String intraId;
	private Integer penaltyTime;
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime releaseTime;

	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime startTime;
	private String reason;

	public RedisPenaltyUser(String intraId, Integer penaltyTime, LocalDateTime releaseTime, LocalDateTime startTime,
		String reason) {
		this.intraId = intraId;
		this.penaltyTime = penaltyTime;
		this.releaseTime = releaseTime;
		this.startTime = startTime;
		this.reason = reason;
	}

	public void updateReleaseTime(LocalDateTime releaseTime, Integer penaltyTime) {
		this.releaseTime = releaseTime;
		this.penaltyTime = penaltyTime;
	}
}
