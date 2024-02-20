package gg.pingpong.data.game;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import gg.pingpong.data.BaseTimeEntity;
import gg.pingpong.data.game.type.TournamentRound;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class TournamentGame extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "game_id")
	private Game game;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "tournament_id")
	private Tournament tournament;

	@NotNull
	@Column(name = "round", length = 30)
	@Enumerated(EnumType.STRING)
	private TournamentRound tournamentRound;

	/**
	 * id 값 제외한 생성자.
	 * <p>
	 *  생성에 따른 Tournament 연관관계 설정을 담당
	 * </p>
	 * @param game
	 * @param tournament
	 * @param tournamentRound
	 */
	@Builder
	public TournamentGame(Game game, Tournament tournament, TournamentRound tournamentRound) {
		tournament.addTournamentGame(this);
		this.game = game;
		this.tournament = tournament;
		this.tournamentRound = tournamentRound;
	}

	/**
	 * TournamentGame의 게임 정보를 업데이트한다.
	 * @param game
	 */
	public void updateGame(Game game) {
		this.game = game;
	}

}
