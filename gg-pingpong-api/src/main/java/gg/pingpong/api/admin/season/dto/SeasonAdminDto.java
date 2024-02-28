package gg.pingpong.api.admin.season.dto;

import java.time.LocalDateTime;

import gg.data.season.Season;
import gg.pingpong.api.admin.season.type.SeasonStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SeasonAdminDto {

	private Long seasonId;
	private String seasonName;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private Integer startPpp;
	private Integer pppGap;
	private String status;

	public SeasonAdminDto(Season season) {
		this.seasonId = season.getId();
		this.seasonName = season.getSeasonName();
		this.startTime = season.getStartTime();
		this.endTime = season.getEndTime();
		this.startPpp = season.getStartPpp();
		this.pppGap = season.getPppGap();
		this.status = getSeasonStatus(season);
	}

	public String getSeasonStatus(Season season) {
		LocalDateTime now = LocalDateTime.now();

		if (now.isAfter(season.getEndTime())) {
			return SeasonStatus.SEASON_PAST.getSeasonStatus();
		} else if (now.isAfter(season.getStartTime()) && now.isBefore((season.getEndTime()))) {
			return SeasonStatus.SEASON_CURRENT.getSeasonStatus();
		} else {
			return SeasonStatus.SEASON_FUTURE.getSeasonStatus();
		}
	}
}
