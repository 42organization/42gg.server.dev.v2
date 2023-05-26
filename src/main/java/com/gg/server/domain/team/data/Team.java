package com.gg.server.domain.team.data;

import com.gg.server.domain.game.data.Game;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

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

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<TeamUser> teamUsers;

    public Team(Game game, Integer score, Boolean win) {
        this.game = game;
        this.score = score;
        this.win = win;
    }

    public void inputScore(int score) {
        this.score = score;
    }

    public void setWin(Boolean win) {
        this.win = win;
    }
}
