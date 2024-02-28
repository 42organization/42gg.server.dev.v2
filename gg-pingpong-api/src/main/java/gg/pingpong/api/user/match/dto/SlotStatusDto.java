package gg.pingpong.api.user.match.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import gg.data.match.type.SlotStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SlotStatusDto {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
	private LocalDateTime startTime;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm", timezone = "Asia/Seoul")
	private LocalDateTime endTime;
	private String status;

	public SlotStatusDto(LocalDateTime startTime, LocalDateTime endTime, SlotStatus status) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status.getCode();
	}

	public SlotStatusDto(LocalDateTime startTime, SlotStatus status, Integer interval) {
		this.startTime = startTime;
		this.endTime = startTime.plusMinutes(interval);
		this.status = status.getCode();
	}

	@Override
	public String toString() {
		return "SlotStatusDto{"
			+ "startTime = " + startTime
			+ "endTime = " + endTime
			+ "status = " + status
			+ "}";
	}
}
