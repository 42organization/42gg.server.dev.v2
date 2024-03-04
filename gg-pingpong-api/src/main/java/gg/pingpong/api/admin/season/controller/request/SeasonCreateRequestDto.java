package gg.pingpong.api.admin.season.controller.request;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import gg.data.season.Season;
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

	public Season toSeason() {
		return Season.builder()
			.seasonName(seasonName)
			.startTime(startTime)
			.startPpp(startPpp)
			.pppGap(pppGap)
			.build();
	}

	public String toString() {
		return "SeasonCreateRequestAdminDto{" + '\''
			+ "seasonName=" + seasonName + '\''
			+ ", startTime=" + startTime
			+ ", startPpp='" + startPpp + '\''
			+ ", pppGap='" + pppGap + '\''
			+ '}';
	}
}
