package com.gg.server.admin.season.dto;

import com.gg.server.domain.season.Season;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class SeasonAdminDto {

    private Long seasonId;
    private String seasonName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer startPpp;
    private Integer pppGap;
    private Integer status;

    public SeasonAdminDto(Season season) {
        Integer status;
        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(season.getEndTime()))
            status = 0; //SEASON_PAST
        else if (now.isAfter(season.getStartTime()) && now.isBefore((season.getEndTime())))
            status = 1; //SEASON_CURRENT
        else
            status = 2; //SEASON_FUTUER

        this.seasonId = season.getId();
        this.seasonName = season.getSeasonName();
        this.startTime = season.getStartTime();
        this.endTime = season.getEndTime();
        this.startPpp = season.getStartPpp();
        this.pppGap = season.getPppGap();
        this.status = status;
    }
}
