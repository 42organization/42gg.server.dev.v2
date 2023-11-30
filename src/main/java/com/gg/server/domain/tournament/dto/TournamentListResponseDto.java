package com.gg.server.domain.tournament.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class TournamentListResponseDto {

    private List<TournamentResponseDto> tournaments;
    private int totalPage;
}
