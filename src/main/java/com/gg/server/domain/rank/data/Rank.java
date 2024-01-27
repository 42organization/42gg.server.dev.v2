package com.gg.server.domain.rank.data;

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

import com.gg.server.admin.user.dto.UserUpdateAdminRequestDto;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.tier.data.Tier;
import com.gg.server.domain.user.data.User;
import com.gg.server.global.utils.BaseTimeEntity;

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

	public void modifyUserRank(UserUpdateAdminRequestDto userUpdateAdminRequestDto) {
		this.ppp = userUpdateAdminRequestDto.getPpp();
		this.wins = userUpdateAdminRequestDto.getWins();
		this.losses = userUpdateAdminRequestDto.getLosses();
	}

	public void modifyUserRank(Integer ppp, int wins, int losses) {
		this.ppp = ppp;
		this.wins = wins;
		this.losses = losses;
	}

	public void updateTier(Tier tier) {
		this.tier = tier;
	}
}
