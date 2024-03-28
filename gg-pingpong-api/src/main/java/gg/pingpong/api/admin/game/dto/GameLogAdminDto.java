package gg.pingpong.api.admin.game.dto;

import java.time.Duration;
import java.time.LocalDateTime;

import gg.admin.repo.game.out.GameTeamUser;
import gg.data.pingpong.game.type.StatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameLogAdminDto {
	private Long gameId;
	private LocalDateTime startAt;
	private String slotTime;
	private String mode;
	private StatusType status;
	private GameTeamAdminDto team1;
	private GameTeamAdminDto team2;

	public GameLogAdminDto(GameTeamUser game) {
		this.gameId = game.getGameId();
		this.startAt = game.getStartTime();
		this.slotTime = game.getEndTime() == null ? null :
			String.valueOf(
				Duration.between(game.getStartTime().toLocalTime(), game.getEndTime().toLocalTime()).toMinutes());
		this.mode = game.getMode().getCode();
		this.team1 = new GameTeamAdminDto(game.getT1IntraId(), game.getT1TeamId(), game.getT1Score(),
			game.getT1IsWin());
		this.team2 = new GameTeamAdminDto(game.getT2IntraId(), game.getT2TeamId(), game.getT2Score(),
			game.getT2IsWin());
		this.status = game.getStatus();
	}
}
