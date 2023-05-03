package com.gg.server.admin.season.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SeasonListAdminResponseDto {
    List<SeasonAdminDto> seasonList;

    public SeasonListAdminResponseDto(List<SeasonAdminDto> seasonList) {
        this.seasonList = seasonList;
    }

}
