package com.gg.server.domain.teamuser;

import com.gg.server.domain.team.Team;
import com.gg.server.domain.user.User;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;

import javax.persistence.*;

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
}
