package com.gg.server.domain.tournament.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class TournamentListResponseDto {

	private List<TournamentResponseDto> tournaments;
	private int totalPage;
}
