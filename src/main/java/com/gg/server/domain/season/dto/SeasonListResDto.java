package com.gg.server.domain.season.dto;

import java.util.List;

public class SeasonListResDto {
    List<SeasonResDto> seasonList;

    public SeasonListResDto(List<SeasonResDto> seasonList) {
        this.seasonList = seasonList;
    }
}
