package com.gg.server.domain.game.data;

import com.gg.server.domain.match.dto.GameAddDto;
import com.gg.server.domain.season.data.Season;
import com.gg.server.domain.game.type.Mode;
import com.gg.server.domain.game.type.StatusType;
import com.gg.server.domain.team.data.Team;
import com.gg.server.global.exception.ErrorCode;
import com.gg.server.global.utils.BusinessChecker;
import java.util.ArrayList;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id")
    private Season season;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10)
    private StatusType status;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "mode", length = 10)
    private Mode mode;

    @NotNull
    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<Team> teams = new ArrayList<>();

    public Game(Season season, StatusType status, Mode mode, LocalDateTime startTime, LocalDateTime endTime) {
        this.season = season;
        this.status = status;
        this.mode = mode;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Game(GameAddDto dto, Integer interval) {
        this.season = dto.getSeason();
        this.status = StatusType.BEFORE;
        this.mode = dto.getMode();
        this.startTime = dto.getStartTime();
        this.endTime = dto.getStartTime().plusMinutes(interval);
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", season=" + season +
                ", status=" + status +
                ", mode=" + mode +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    public void updateStatus() {
        if (status == StatusType.BEFORE) {
            this.status = StatusType.LIVE;
        } else if (status == StatusType.LIVE) {
            this.status = StatusType.WAIT;
        } else {
            this.status = StatusType.END;
        }
    }

    public void addTeam(Team team) {
        BusinessChecker.mustNotNull(team, ErrorCode.NULL_POINT);
        BusinessChecker.mustNotExceed(1, teams, ErrorCode.TEAM_SIZE_EXCEED);
        BusinessChecker.mustNotContains(team, teams, ErrorCode.TEAM_DUPLICATION);
        this.teams.add(team);
    }

    public Optional<Team> getWinnerTeam() {
        return this.teams.stream()
            .filter(team -> Boolean.TRUE.equals(team.getWin()))
            .findAny();
    }
}
