package com.gg.server.domain.tournament.dto;

import com.gg.server.domain.tournament.data.TournamentUser;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class TournamentUserListResponseDto {
    private List<TournamentUserResponseDto> users;

    public TournamentUserListResponseDto(List<TournamentUser> tournamentUsers){
        users = new ArrayList<>();
        for (TournamentUser tournamentUser : tournamentUsers) {
            users.add(new TournamentUserResponseDto(tournamentUser));
        }
        users.sort(Comparator.comparing(TournamentUserResponseDto::getIsJoined).reversed().
                thenComparing(TournamentUserResponseDto::getRegisteredDate));
    }
}
