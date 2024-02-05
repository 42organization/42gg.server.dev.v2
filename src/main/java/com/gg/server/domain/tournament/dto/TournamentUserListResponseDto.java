package com.gg.server.domain.tournament.dto;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.gg.server.data.game.TournamentUser;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class TournamentUserListResponseDto {
	private List<TournamentUserResponseDto> users;

	public TournamentUserListResponseDto(List<TournamentUser> tournamentUsers) {
		users = new ArrayList<>();
		for (TournamentUser tournamentUser : tournamentUsers) {
			users.add(new TournamentUserResponseDto(tournamentUser));
		}
		users.sort(Comparator.comparing(TournamentUserResponseDto::getIsJoined).reversed()
			.thenComparing(TournamentUserResponseDto::getRegisteredDate));
	}
}
