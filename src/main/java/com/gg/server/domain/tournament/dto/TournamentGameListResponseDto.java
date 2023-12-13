package com.gg.server.domain.tournament.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TournamentGameListResponseDto {
    private Long tournamentId;
    private List<TournamentGameResDto> games;

    public TournamentGameListResponseDto(Long tournamentId, List<TournamentGameResDto> games) {
        this.tournamentId = tournamentId;
        this.games = games;
    }

    @Override
    public String toString() {
        return "TournamentGameListResponseDto{" +
                "tournamentId=" + tournamentId +
                ", games=" + games.toString() +
                '}';
    }
}
