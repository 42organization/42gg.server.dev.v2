package com.gg.server.domain.season.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class SeasonListResDto {
    List<SeasonResDto> seasonList;

    public SeasonListResDto(List<SeasonResDto> seasonList) {
        this.seasonList = seasonList;
    }
}
