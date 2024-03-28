package gg.pingpong.api.user.tournament.controller.request;

import gg.data.pingpong.tournament.type.TournamentStatus;
import gg.data.pingpong.tournament.type.TournamentType;
import gg.utils.dto.PageRequestDto;
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
