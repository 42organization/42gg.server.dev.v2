package com.gg.server.admin.season.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum SeasonStatus {

    SEASON_PAST("PAST"),
    SEASON_CURRENT("CURRENT"),
    SEASON_FUTUER("FUTUER");

    private final String Sesonstauts;

}
