package com.gg.server.data.game;

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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
public class PChange extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id")
	private Game game;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@NotNull
	@Column(name = "ppp_result")
	private Integer pppResult;

	@NotNull
	@Column(name = "exp")
	private Integer exp;

	@NotNull
	@Column(name = "is_checked")
	private Boolean isChecked;

	public PChange(Game game, User user, Integer pppResult, Boolean isChecked) {
		this.game = game;
		this.user = user;
		this.pppResult = pppResult;
		this.exp = user.getTotalExp();
		this.isChecked = isChecked;
	}

	public void checkPChange() {
		this.isChecked = true;
	}

	public void updatePPP(Integer ppp) {
		this.pppResult = ppp;
	}

	@Override
	public String toString() {
		return "PChange{"
			+ "id=" + id
			+ ", game=" + game
			+ ", user=" + user
			+ ", pppResult=" + pppResult
			+ ", exp=" + exp
			+ ", isChecked=" + isChecked
			+ '}';
	}
}
