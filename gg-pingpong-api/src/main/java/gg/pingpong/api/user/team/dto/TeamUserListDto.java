package gg.pingpong.api.user.team.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TeamUserListDto {
	Long teamId;

	List<TeamUserInfoDto> players;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	Boolean isWin;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	Integer score;

	public TeamUserListDto(List<TeamUserInfoDto> players, Boolean isWin, Integer score) {
		this.isWin = isWin;
		this.score = score;
		this.players = players;
	}

	public TeamUserListDto(Long teamId, List<TeamUserInfoDto> players, Boolean isWin, Integer score) {
		this.teamId = teamId;
		this.players = players;
		this.isWin = isWin;
		this.score = score == -1 ? null : score;
	}

	@Override
	public String toString() {
		return "TeamUserListDto{"
			+ "players=" + players
			+ '}';
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof TeamUserListDto)) {
			return false;
		} else {
			TeamUserListDto other = (TeamUserListDto)obj;
			return (isWin == other.getIsWin() || this.isWin.equals(other.getIsWin()))
				&& (score == other.getScore() || this.score.equals(other.getScore()))
				&& this.players.equals(other.getPlayers());
		}
	}
}
