package com.gg.server.admin.tournament.dto;

import com.gg.server.domain.team.dto.TeamReqDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TournamentGameUpdateRequestDto {

    @NotNull
    private Long tournamentGameId;

    private Long nextTournamentGameId;
    @NotNull
    @Valid
    private TeamReqDto team1;
    @NotNull
    @Valid
    private TeamReqDto team2;

    @Override
    public String toString() {
        return "TournamentGameUpdateReqDto{" +
                "tournamentGameId=" + tournamentGameId +
                ", nextTournamentGameId=" + nextTournamentGameId +
                ", team1=" + team1 +
                ", team2=" + team2 +
                '}';
    }
}
