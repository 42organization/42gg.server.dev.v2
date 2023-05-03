package com.gg.server.domain.team.data;

import com.gg.server.domain.game.data.Game;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(name = "score")
    private Integer score;

    @Column(name = "win")
    private Boolean win;

    public Team(Game game, Integer score, Boolean win) {
        this.game = game;
        this.score = score;
        this.win = win;
    }
}
