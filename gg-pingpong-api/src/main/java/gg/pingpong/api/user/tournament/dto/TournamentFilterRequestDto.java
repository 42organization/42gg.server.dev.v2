package gg.pingpong.api.user.tournament.dto;

import com.gg.server.data.game.type.TournamentStatus;
import com.gg.server.data.game.type.TournamentType;
import com.gg.server.global.dto.PageRequestDto;

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
