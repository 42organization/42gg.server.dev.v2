package com.gg.server.domain.tournament.data;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.tournament.type.TournamentRound;
import com.gg.server.global.utils.BaseTimeEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@AllArgsConstructor
public class TournamentGame extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
