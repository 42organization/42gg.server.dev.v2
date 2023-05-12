package com.gg.server.admin.season.dto;

import com.gg.server.admin.season.type.SeasonStatus;
import com.gg.server.domain.season.data.Season;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
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

        if (now.isAfter(season.getEndTime()))
            return SeasonStatus.SEASON_PAST.getSesonstauts();
        else if (now.isAfter(season.getStartTime()) && now.isBefore((season.getEndTime())))
            return SeasonStatus.SEASON_CURRENT.getSesonstauts();
        else
            return SeasonStatus.SEASON_FUTURE.getSesonstauts();
    }
}
