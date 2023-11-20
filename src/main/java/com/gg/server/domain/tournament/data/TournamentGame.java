package com.gg.server.domain.tournament.data;

import com.gg.server.domain.game.data.Game;
import com.gg.server.domain.tournament.type.TournamentRound;
import com.gg.server.global.utils.BaseTimeEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity
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
     * id 값 제외한 생성자
     * @param game
     * @param tournament
     * @param tournamentRound
     */
    public TournamentGame(Game game, Tournament tournament, TournamentRound tournamentRound) {
        this.game = game;
        this.tournament = tournament;
        this.tournamentRound = tournamentRound;
    }
}
