package com.gg.server.domain.team.data;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.gg.server.domain.user.data.User;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.utils.BusinessChecker;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Getter
@ToString
public class TeamUser {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	public TeamUser(Team team, User user) {
		this.team = team;
		this.user = user;
		team.addTeamUser(this);
	}

	public void updateUser(User user) {
		BusinessChecker.mustNotNull(user, ErrorCode.NULL_POINT);
		this.user = user;
	}
}
