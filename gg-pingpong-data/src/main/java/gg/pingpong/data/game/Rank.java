package gg.pingpong.data.game;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.DynamicUpdate;

import gg.pingpong.data.BaseTimeEntity;
import gg.pingpong.data.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ranks")
@DynamicUpdate
public class Rank extends BaseTimeEntity implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "season_id")
	private Season season;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tier_id")
	private Tier tier;

	@NotNull
	@Column(name = "ppp")
	private Integer ppp;

	@NotNull
	@Column(name = "wins")
	private Integer wins;

	@NotNull
	@Column(name = "losses")
	private Integer losses;

	@Column(name = "status_message", length = 30)
	private String statusMessage;

	public static Rank from(User user, Season season, Integer ppp, Tier tier) {
		return Rank.builder()
			.user(user)
			.ppp(ppp)
			.season(season)
			.wins(0)
			.losses(0)
			.statusMessage("")
			.tier(tier)
			.build();
	}

	@Builder
	public Rank(User user, Season season, Integer ppp, Integer wins,
		Integer losses, String statusMessage, Tier tier) {
		this.user = user;
		this.season = season;
		this.ppp = ppp;
		this.wins = wins;
		this.losses = losses;
		this.statusMessage = statusMessage;
		this.tier = tier;
	}

	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public void modifyUserRank(Integer ppp, int wins, int losses) {
		this.ppp = ppp;
		this.wins = wins;
		this.losses = losses;
	}

	public void updateTier(Tier tier) {
		this.tier = tier;
	}

	public boolean isParticipated() {
		return wins + losses > 0;
	}
}
