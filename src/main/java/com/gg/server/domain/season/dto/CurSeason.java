package com.gg.server.domain.season.dto;

import com.gg.server.domain.season.data.Season;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CurSeason {
    private Long id;
    private String seasonName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer startPpp;
    private Integer pppGap;

    public CurSeason(Season season) {
        this.id = season.getId();
        this.seasonName = season.getSeasonName();
        this.startTime = season.getStartTime();
        this.endTime = season.getEndTime();
        this.startPpp = season.getStartPpp();
        this.pppGap = season.getPppGap();
    }
}
