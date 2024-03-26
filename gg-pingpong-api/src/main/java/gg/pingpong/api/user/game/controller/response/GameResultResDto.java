package gg.pingpong.api.user.game.controller.response;

import java.time.LocalDateTime;
import java.util.Arrays;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import gg.data.pingpong.game.type.Mode;
import gg.pingpong.api.user.game.dto.TeamUserInfoDto;
import gg.pingpong.api.user.game.dto.TeamUserListDto;
import gg.repo.game.out.GameTeamUser;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GameResultResDto {
	private Long gameId;
	private String status;
	private Mode mode;
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	private LocalDateTime time;
	private TeamUserListDto team1;
	private TeamUserListDto team2;

	public GameResultResDto(GameTeamUser game) {
		this.gameId = game.getGameId();
		this.status = game.getStatus().name(); //name -> 대문자
		this.time = game.getStartTime();
		this.mode = game.getMode();
		if (mode == Mode.NORMAL) {
			team1 = new TeamUserListDto(Arrays.asList(
				new TeamUserInfoDto(game.getT1IntraId(), game.getT1Image(), game.getT1Exp(), null, null)), null, null);
			team2 = new TeamUserListDto(Arrays.asList(
				new TeamUserInfoDto(game.getT2IntraId(), game.getT2Image(), game.getT2Exp(), null, null)), null, null);
		} else if (mode == Mode.RANK) {
			team1 = new TeamUserListDto(Arrays.asList(
				new TeamUserInfoDto(game.getT1IntraId(), game.getT1Image(), game.getT1Exp(), game.getT1Wins(),
					game.getT1Losses())), game.getT1IsWin(), game.getT1Score());
			team2 = new TeamUserListDto(Arrays.asList(
				new TeamUserInfoDto(game.getT2IntraId(), game.getT2Image(), game.getT2Exp(), game.getT2Wins(),
					game.getT2Losses())), game.getT2IsWin(), game.getT2Score());
		} else if (mode == Mode.TOURNAMENT) {
			team1 = new TeamUserListDto(
				game.getT1TeamId(),
				Arrays.asList(
					new TeamUserInfoDto(game.getT1IntraId(), game.getT1Image(), game.getT1Exp(), game.getT1Wins(),
						game.getT1Losses())),
				game.getT1IsWin(), game.getT1Score());
			team2 = new TeamUserListDto(game.getT2TeamId(),
				Arrays.asList(
					new TeamUserInfoDto(game.getT2IntraId(), game.getT2Image(), game.getT2Exp(), game.getT2Wins(),
						game.getT2Losses())),
				game.getT2IsWin(), game.getT2Score());
		}
	}

	@Override
	public String toString() {
		return "NormalGameResDto{"
			+ "gameId=" + gameId
			+ ", status='" + status + '\''
			+ ", time=" + time
			+ ", team1=" + team1
			+ ", team2=" + team2
			+ '}';
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof GameResultResDto)) {
			return false;
		} else {
			GameResultResDto other = (GameResultResDto)obj;
			return this.gameId.equals(other.getGameId())
				&& this.status.equals(other.getStatus())
				&& this.mode.equals(other.getMode())
				&& this.time.equals(other.getTime())
				&& this.team1.equals(other.getTeam1())
				&& this.team2.equals(other.getTeam2());
		}
	}
}
