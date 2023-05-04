package com.gg.server.domain.season.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SeasonListResDto {
    List<SeasonResDto> seasonList;

    public SeasonListResDto(List<SeasonResDto> seasonList) {
        this.seasonList = seasonList;
    }
}
