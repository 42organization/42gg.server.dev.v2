package com.gg.server.admin.season.dto;

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
    private Integer status;

    public SeasonAdminDto(Season season) {
        this.seasonId = season.getId();
        this.seasonName = season.getSeasonName();
        this.startTime = season.getStartTime();
        this.endTime = season.getEndTime();
        this.startPpp = season.getStartPpp();
        this.pppGap = season.getPppGap();
        this.status = setSeasonStatus(season);
    }

    private Integer setSeasonStatus(Season season) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(season.getEndTime()))
            return 0; //SEASON_PAST
        else if (now.isAfter(season.getStartTime()) && now.isBefore((season.getEndTime())))
            return 1; //SEASON_CURRENT
        else
            return 2; //SEASON_FUTUER
    }
}
