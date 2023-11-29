package com.gg.server.domain.tournament.dto;

import com.gg.server.domain.tournament.type.TournamentStatus;
import com.gg.server.domain.tournament.type.TournamentType;
import com.gg.server.domain.tournament.validation.EnumValue;
import com.gg.server.global.dto.PageRequestDto;
import lombok.Getter;

import javax.annotation.Nullable;

@Getter
public class TournamentFilterRequestDto extends PageRequestDto {

    @EnumValue(enumClass = TournamentType.class, message = "plz. check tournamentType")
    private String type;

    @EnumValue(enumClass = TournamentStatus.class, message = "plz. check tournamentStatus")
    private String status;

    public TournamentFilterRequestDto(Integer page, Integer size, @Nullable String type, @Nullable String status) {
        super(page, size);
        this.type = type;
        this.status = status;
    }
}
