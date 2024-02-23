package gg.pingpong.api.user.game.dto;

import java.util.List;

import gg.pingpong.repo.game.GameTeamUserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class MatchTeamsInfoDto {
	private TeamInfo myTeam;
	private TeamInfo enemyTeam;

	public MatchTeamsInfoDto(List<GameTeamUserInfo> infos, Long teamId) {
		if (teamId == null) {
			return;
		}
		myTeam = new TeamInfo();
		enemyTeam = new TeamInfo();
		for (GameTeamUserInfo info :
			infos) {
			if (teamId.equals(info.getTeamId())) {
				myTeam.setTeam(info.getTeamId(), info.getScore());
				myTeam.addPlayer(info);
			} else {
				enemyTeam.setTeam(info.getTeamId(), info.getScore());
				enemyTeam.addPlayer(info);
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof MatchTeamsInfoDto)) {
			return false;
		} else {
			MatchTeamsInfoDto other = (MatchTeamsInfoDto)obj;
			return this.myTeam.equals(other.getMyTeam())
				&& this.enemyTeam.equals(other.getEnemyTeam());
		}
	}
}
