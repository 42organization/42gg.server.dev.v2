package gg.pingpong.api.user.tournament.dto;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public class TournamentListResponseDto {

	private List<TournamentResponseDto> tournaments;
	private int totalPage;

	public TournamentListResponseDto(Page<TournamentResponseDto> tournamentsDto) {
		tournaments = tournamentsDto.getContent();
		totalPage = tournamentsDto.getTotalPages();
	}
}
