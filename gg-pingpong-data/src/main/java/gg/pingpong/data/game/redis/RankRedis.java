package gg.pingpong.data.game.redis;

import java.io.Serializable;

import org.springframework.data.redis.core.RedisHash;

import gg.pingpong.data.game.Rank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@RedisHash("rank")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RankRedis implements Serializable {
	private Long userId;
	private String intraId;
	private int ppp;
	private int wins;
	private int losses;
	private String statusMessage;
	private String tierImageUri;
	private String textColor;

	public void updateRank(int changePpp, int wins, int losses) {
		this.ppp += changePpp;
		this.wins = wins;
		this.losses = losses;
	}

	public void updateTierImage(String tierImageUri) {
		this.tierImageUri = tierImageUri;
	}

	public void changedRank(int ppp, int wins, int losses) {
		this.ppp = ppp;
		this.wins = wins;
		this.losses = losses;
	}

	public void setStatusMessage(String msg) {
		this.statusMessage = msg;
	}

	public static RankRedis from(Long id, String intraId, String textColor, Integer ppp, String tierImageUri) {
		RankRedis rankRedis = RankRedis.builder()
			.userId(id)
			.intraId(intraId)
			.ppp(ppp)
			.wins(0)
			.losses(0)
			.statusMessage("")
			.tierImageUri(tierImageUri)
			.textColor(textColor)
			.build();
		return rankRedis;
	}

	public static RankRedis from(Rank rank) {
		RankRedis rankRedis = RankRedis.builder()
			.userId(rank.getUser().getId())
			.intraId(rank.getUser().getIntraId())
			.ppp(rank.getPpp())
			.wins(rank.getWins())
			.losses(rank.getLosses())
			.statusMessage(rank.getStatusMessage())
			.tierImageUri(rank.getTier().getImageUri())
			.textColor(rank.getUser().getTextColor())
			.build();
		return rankRedis;
	}

	@Override
	public String toString() {
		return "RankRedis{"
			+ "userId=" + userId
			+ ", intraId='" + intraId + '\''
			+ ", ppp=" + ppp
			+ ", wins=" + wins
			+ ", losses=" + losses
			+ ", statusMessage='" + statusMessage + '\''
			+ ", tierImageUri='" + tierImageUri + '\''
			+ ", textColor='" + textColor + '\''
			+ '}';
	}
}
