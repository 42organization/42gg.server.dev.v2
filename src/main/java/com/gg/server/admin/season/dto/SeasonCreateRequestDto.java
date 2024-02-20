package com.gg.server.admin.season.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.gg.server.data.game.Season;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SeasonCreateRequestDto {
	@NotNull(message = "plz. seasonName")
	private String seasonName;

	@NotNull(message = "plz. startTime")
	@Future(message = "불가능한 예약시점입니다.")
	@DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
	private LocalDateTime startTime;

	@NotNull(message = "plz. startPpp")
	private Integer startPpp;

	@NotNull(message = "plz. pppGap")
	private Integer pppGap;

	public String toString() {
		return "SeasonCreateRequestAdminDto{" + '\''
			+ "seasonName=" + seasonName + '\''
			+ ", startTime=" + startTime
			+ ", startPpp='" + startPpp + '\''
			+ ", pppGap='" + pppGap + '\''
			+ '}';
	}

	public Season toEntity() {
		return Season.builder()
			.seasonName(this.seasonName)
			.startTime(this.startTime)
			.startPpp(this.startPpp)
			.pppGap(this.pppGap)
			.build();
	}
}
