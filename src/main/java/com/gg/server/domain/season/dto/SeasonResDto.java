package com.gg.server.domain.season.dto;

import com.gg.server.domain.season.data.Season;

public class SeasonResDto {
    private Long id;
    private String name;

    public SeasonResDto(Season season) {
        this.id = season.getId();
        this.name = season.getSeasonName();
    }
}
