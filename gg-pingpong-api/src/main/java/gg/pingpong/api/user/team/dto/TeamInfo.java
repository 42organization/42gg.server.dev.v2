package gg.pingpong.api.user.team.dto;

import java.util.ArrayList;
import java.util.List;

import com.gg.server.domain.game.dto.GameTeamUserInfo;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TeamInfo {
	private Integer teamScore;
	private Long teamId;
	private List<TeamUserInfoDto> players;

	public TeamInfo() {
		this.players = new ArrayList<>();
	}

	public void setTeam(Long teamId, Integer teamScore) {
		this.teamId = teamId;
		this.teamScore = teamScore;
	}

	public void addPlayer(GameTeamUserInfo info) {
		this.players.add(new TeamUserInfoDto(info.getIntraId(), info.getImage(), info.getExp(), null, null));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof TeamInfo)) {
			return false;
		} else {
			TeamInfo other = (TeamInfo)obj;
			return this.teamScore.equals(other.getTeamScore())
				&& this.teamId.equals(other.getTeamId())
				&& this.players.equals(other.getPlayers());
		}
	}
}
