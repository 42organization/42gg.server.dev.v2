package com.gg.server.admin.season.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SeasonListAdminResponseDto {
    List<SeasonAdminDto> seasonList;
}
