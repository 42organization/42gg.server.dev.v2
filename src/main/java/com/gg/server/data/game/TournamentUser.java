package com.gg.server.data.game;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.gg.server.data.user.User;
import com.gg.server.global.utils.BaseTimeEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class TournamentUser extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tournament_id")
	private Tournament tournament;

	@NotNull
	@Column(name = "is_joined")
	private Boolean isJoined;

	@NotNull
	@Column(name = "register_time")
	private LocalDateTime registerTime;

	/**
	 * 생성자이며, 빌더이자, 생성 연관관계를 담당한다.
	 */
	@Builder
	public TournamentUser(User user, Tournament tournament, boolean isJoined, LocalDateTime registerTime) {
		tournament.addTournamentUser(this);
		this.user = user;
		this.tournament = tournament;
		this.isJoined = isJoined;
		this.registerTime = registerTime;
	}

	/**
	 * 연관관계 편의 메서드, 삭제 책임을 가진다.
	 */
	public void deleteTournament() {
		tournament.deleteTournamentUser(this);
		this.tournament = null;
	}

	public void updateIsJoined(boolean isJoined) {
		this.isJoined = isJoined;
	}
}
