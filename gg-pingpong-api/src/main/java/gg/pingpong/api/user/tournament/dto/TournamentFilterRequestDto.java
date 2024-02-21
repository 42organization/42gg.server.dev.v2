package gg.pingpong.api.user.tournament.dto;

import gg.pingpong.api.global.dto.PageRequestDto;
import gg.pingpong.data.game.type.TournamentStatus;
import gg.pingpong.data.game.type.TournamentType;
import lombok.Getter;

@Getter
public class TournamentFilterRequestDto extends PageRequestDto {

	private TournamentType type;

	private TournamentStatus status;

	public TournamentFilterRequestDto(Integer page, Integer size, TournamentType type, TournamentStatus status) {
		super(page, size);
		this.type = type;
		this.status = status;
	}
}
