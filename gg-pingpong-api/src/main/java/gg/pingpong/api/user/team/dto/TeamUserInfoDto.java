package gg.pingpong.api.user.team.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import gg.pingpong.api.global.utils.ExpLevelCalculator;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TeamUserInfoDto {
	private String intraId;
	private String userImageUri;
	private Integer level;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer wins;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer losses;

	public TeamUserInfoDto(String intraId, String userImageUri, Integer exp, Integer wins, Integer losses) {
		this.intraId = intraId;
		this.userImageUri = userImageUri;
		this.level = ExpLevelCalculator.getLevel(exp);
		this.wins = wins;
		this.losses = losses;
	}

	@Override
	public String toString() {
		return "TeamUserInfoDto{"
			+ "intraId='" + intraId + '\''
			+ ", userImageUri='" + userImageUri + '\''
			+ ", level=" + level
			+ '}';
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (!(obj instanceof TeamUserInfoDto)) {
			return false;
		} else {
			TeamUserInfoDto other = (TeamUserInfoDto)obj;
			return this.intraId.equals(other.getIntraId())
				&& this.level.equals(other.getLevel())
				&& this.userImageUri.equals(other.getUserImageUri())
				&& (this.wins == other.getWins() || this.wins.equals(other.getWins()))
				&& (this.losses == other.getLosses() || this.losses.equals(other.getLosses()));
		}
	}
}
