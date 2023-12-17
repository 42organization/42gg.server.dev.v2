package com.gg.server.domain.tournament.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TournamentType {
    ROOKIE("rookie", "초보"),
    MASTER("master", "고수"),
    CUSTOM("custom", "커스텀");

    private final String code;
    private final String desc;
}
