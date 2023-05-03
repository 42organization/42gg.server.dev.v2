package com.gg.server.domain.teamuser.data;

import com.gg.server.domain.team.data.Team;
import com.gg.server.domain.user.User;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    }
}
