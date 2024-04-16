package gg.pingpong.api.user.game.dto;

import java.time.LocalDateTime;
import java.util.List;

import gg.data.pingpong.game.type.Mode;
import gg.data.pingpong.game.type.StatusType;
import gg.repo.game.out.GameTeamUserInfo;
import gg.utils.exception.game.GameDataConsistencyException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@NoArgsConstructor
@ToString
@Slf4j
public class GameTeamInfo {
	private Mode mode;
	private Long gameId;
	private LocalDateTime startTime;
	private StatusType status;
	private Boolean isScoreExist;
	private MatchTeamsInfoDto matchTeamsInfo;

	public GameTeamInfo(List<GameTeamUserInfo> infos, Long userId) {
		this.mode = infos.get(0).getMode();
		this.gameId = infos.get(0).getGameId();
		this.startTime = infos.get(0).getStartTime();
		this.status = infos.get(0).getStatus();
		Long myTeamId = null;
		for (GameTeamUserInfo info :
			infos) {
			if (info.getScore() > -1) {
				this.isScoreExist = true;
			}
			if (!this.mode.equals(info.getMode()) || !this.gameId.equals(info.getGameId())
				|| !this.startTime.equals(info.getStartTime()) || !this.status.equals(info.getStatus())) {
				log.error("data error: gid 1: ", infos.get(0).getGameId(), ", gid 2:", infos.get(1).getGameId());
				throw new GameDataConsistencyException();
			}
			if (info.getUserId().equals(userId)) {
				myTeamId = info.getTeamId();
			}
		}
		this.matchTeamsInfo = new MatchTeamsInfoDto(infos, myTeamId);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof GameTeamInfo)) {
			return false;
		} else {
			GameTeamInfo other = (GameTeamInfo)obj;
			return this.status.equals(other.getStatus())
				&& this.gameId.equals(other.getGameId())
				&& this.isScoreExist.equals(other.getIsScoreExist())
				&& this.mode.equals(other.getMode())
				&& this.startTime.equals(other.getStartTime())
				&& this.matchTeamsInfo.equals(other.getMatchTeamsInfo());
		}
	}
}
