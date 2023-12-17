package com.gg.server.domain.team.data;

import com.gg.server.domain.game.data.Game;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.utils.BusinessChecker;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private List<TeamUser> teamUsers = new ArrayList<>();

    public Team(Game game, Integer score, Boolean win) {
        this.game = game;
        this.score = score;
        this.win = win;
        game.addTeam(this);
    }

    public void updateScore(int score, Boolean win) {
        this.score = score;
        this.win = win;
    }

    public void addTeamUser(TeamUser teamUser) {
        BusinessChecker.mustNotNull(teamUser, ErrorCode.NULL_POINT);
        BusinessChecker.mustNotExceed(1, teamUsers, ErrorCode.TEAM_USER_EXCEED);
        BusinessChecker.mustNotContains(teamUser, teamUsers, ErrorCode.TEAM_USER_ALREADY_EXIST);
        this.teamUsers.add(teamUser);
    }
}
